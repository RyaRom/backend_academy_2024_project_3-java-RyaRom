package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class DefaultAnalyzer implements Analyzer {
    private final LocalDateTime startingDate;

    private final LocalDateTime endDate;

    private final Predicate<LogInstance> filter;

    private final Comparator<Entry<String, Long>> comparatorForPairs =
        Comparator.comparingLong(
            (Entry<String, Long> entry) -> entry.getValue()
        ).reversed();

    private LogAnalysisResult processLogs(Stream<LogInstance> logs) {
        LogAnalysisResult result = new LogAnalysisResult();
        logs.parallel().forEach(line -> {
            if (logIsInRequirements(line)) {
                processLogInstance(line, result);
            }
        });
        return result;
    }

    private void processLogInstance(LogInstance line, LogAnalysisResult result) {
        if (isError(line.status())) {
            result.error.incrementAndGet();
        }
        result.byteSum.setOpaque(result.byteSum.longValue() + line.bodyBitesSent());
        result.count.incrementAndGet();

        result.bytes.add(line.bodyBitesSent());
        String method = getRequestMethod(line.request());
        String resource = getRequestPath(line.request());
        result.resources.compute(resource, (k, v) -> (v == null) ? 1L : v + 1);
        result.requestMethods.compute(method, (k, v) -> (v == null) ? 1L : v + 1);
        result.responseCodes.compute(line.status(), (k, v) -> (v == null) ? 1L : v + 1);
        result.uniqueIpAddresses.compute(line.remoteAddress(), (k, v) -> (v == null) ? 1L : v + 1);
        result.uniqueUserAgents.compute(line.httpUserAgent(), (k, v) -> (v == null) ? 1L : v + 1);
    }

    private boolean logIsInRequirements(LogInstance line) {
        return filter.test(line)
            && line.timeLocal().isAfter(startingDate)
            && line.timeLocal().isBefore(endDate);
    }

    private LogReport buildReport(LogAnalysisResult result, List<String> fileNames) {
        return LogReport.builder()
            .startingDate(startingDate)
            .endDate(endDate)
            .requestCount(result.count.get())
            .fileNames(fileNames)
            .responseCodes(
                result.responseCodes
                    .entrySet()
                    .stream()
                    .sorted(comparatorForPairs)
                    .toList()
            )
            .resources(
                result.resources
                    .entrySet()
                    .stream()
                    .sorted(comparatorForPairs)
                    .toList()
            )
            .errorRate(
                1.0 * result.error.get() / result.count.get()
            )
            .requestMethods(
                result.requestMethods
                    .entrySet()
                    .stream()
                    .sorted(comparatorForPairs)
                    .toList()
            )
            .averageResponseByteSize(
                result.count.longValue() == 0L ? 0 : result.byteSum.longValue() / result.count.longValue()
            )
            .uniqueUserAgents(
                result.uniqueUserAgents
                    .entrySet()
                    .stream()
                    .sorted(comparatorForPairs)
                    .toList()
            )
            .uniqueIpAddresses(
                result.uniqueIpAddresses
                    .entrySet()
                    .stream()
                    .sorted(comparatorForPairs)
                    .toList()
            )
            .response95pByteSize(calculate95pBytes(result.bytes))
            .build();
    }

    @Override
    public LogReport analyze(Stream<LogInstance> logs, String path) {
        List<String> fileNames;
        if (isURL(path)) {
            fileNames = List.of(path);
        } else {
            fileNames = getFileNames(path);
        }

        LogAnalysisResult result = processLogs(logs);
        return buildReport(result, fileNames);
    }

    private List<String> getFileNames(String path) {
        try {
            return Files.walk(Path.of(path))
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .toList();
        } catch (IOException e) {
            log.error("Error reading file names {}", path);
            return List.of("Error reading file names");
        }
    }

    @SuppressWarnings("MagicNumber")
    private long calculate95pBytes(List<Long> bytes) {
        Collections.sort(bytes);
        int index = (int) Math.ceil(0.95 * bytes.size()) - 1;
        if (index >= 0 && index < bytes.size()) {
            return bytes.get(index);
        } else {
            return 0;
        }
    }

    private String getRequestMethod(String request) {
        var arr = request.split(" ");
        if (arr.length < 1) {
            return "";
        }
        return arr[0];
    }

    private String getRequestPath(String request) {
        var arr = request.split(" ");
        if (arr.length < 2) {
            return "";
        }
        return arr[1];
    }

    private boolean isURL(String path) {
        try {
            new URI(path).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isError(String code) {
        return code.startsWith("4") || code.startsWith("5");
    }

    @Getter
    private static final class LogAnalysisResult {
        private final AtomicLong count = new AtomicLong();

        private final AtomicLong error = new AtomicLong();

        private final AtomicLong byteSum = new AtomicLong();

        private final List<Long> bytes = new CopyOnWriteArrayList<>();

        private final Map<String, Long> resources = new ConcurrentHashMap<>();

        private final Map<String, Long> responseCodes = new ConcurrentHashMap<>();

        private final Map<String, Long> requestMethods = new ConcurrentHashMap<>();

        private final Map<String, Long> uniqueIpAddresses = new ConcurrentHashMap<>();

        private final Map<String, Long> uniqueUserAgents = new ConcurrentHashMap<>();
    }
}
