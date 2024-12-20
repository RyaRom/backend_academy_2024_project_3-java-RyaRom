package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import backend.academy.service.parsing.GlobParser;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static backend.academy.service.MainService.CONSOLE_WRITER;

@Log4j2
@RequiredArgsConstructor
public class DefaultAnalyzer implements Analyzer {
    private final OffsetDateTime startingDate;

    private final OffsetDateTime endDate;

    private final Predicate<LogInstance> filter;

    private final Charset encoding;

    private final Comparator<Entry<String, Long>> comparatorForPairs =
        Comparator.comparingLong(
            (Entry<String, Long> entry) -> entry.getValue()
        ).reversed();

    private void processLogs(Stream<LogInstance> logs, LogAnalysisResult result) {
        logs.parallel().forEach(line -> {
            if (logIsInRequirements(line)) {
                processLogInstance(line, result);
            }
        });
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
            && (line.timeLocal().isAfter(startingDate) || line.timeLocal().isEqual(startingDate))
            && (line.timeLocal().isBefore(endDate) || line.timeLocal().isEqual(endDate));
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
    public LogReport analyze(Stream<Stream<LogInstance>> logs, String path) {
        List<String> fileNames;
        if (isURL(path)) {
            fileNames = List.of(path);
        } else {
            fileNames = getFileNames(path);
        }

        LogAnalysisResult result = new LogAnalysisResult();

        logs.forEach(stream -> {
            try {
                processLogs(stream, result);
            } catch (UncheckedIOException e) {
                log.error("Error while parsing file {}", path, e);
                CONSOLE_WRITER.println("Error while file reading. Encoding must be " + encoding);
            }
        });

        return buildReport(result, fileNames);
    }

    private List<String> getFileNames(String path) {
        var paths = new GlobParser().getNormalPaths(path);
        return paths.stream()
            .filter(Files::isRegularFile)
            .map(p -> p.getFileName().toString())
            .toList();
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
