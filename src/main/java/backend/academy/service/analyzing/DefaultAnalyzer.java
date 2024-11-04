package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class DefaultAnalyzer implements Analyzer {
    private final LocalDateTime startingDate;

    private final LocalDateTime endDate;

    @Override
    public LogReport analyze(Stream<LogInstance> logs, String path) {
        List<String> fileNames;
        if (isURL(path)) {
            fileNames = List.of(path);
        } else {
            fileNames = getFileNames(path);
        }
        AtomicLong count = new AtomicLong();
        AtomicLong error = new AtomicLong();
        AtomicLong byteSum = new AtomicLong();
        List<Long> bytes = new CopyOnWriteArrayList<>();
        Map<String, Long> resources = new ConcurrentHashMap<>();
        Map<String, Long> responseCodes = new ConcurrentHashMap<>();
        Map<String, Long> requestMethods = new ConcurrentHashMap<>();
        Map<String, Long> uniqueIpAddresses = new ConcurrentHashMap<>();
        Map<String, Long> uniqueUserAgents = new ConcurrentHashMap<>();

        logs.parallel().forEach(line -> {
            if (line.timeLocal().isAfter(startingDate)
                && line.timeLocal().isBefore(endDate)) {
                if (isError(line.status())) {
                    error.incrementAndGet();
                }
                byteSum.setOpaque(byteSum.longValue() + line.bodyBitesSent());
                count.incrementAndGet();

                bytes.add(line.bodyBitesSent());
                String method = getRequestMethod(line.request());
                String resource = getRequestPath(line.request());
                resources.compute(resource, (k, v) -> (v == null) ? 1L : v + 1);
                requestMethods.compute(method, (k, v) -> (v == null) ? 1L : v + 1);
                responseCodes.compute(line.status(), (k, v) -> (v == null) ? 1L : v + 1);
                uniqueIpAddresses.compute(line.remoteAddress(), (k, v) -> (v == null) ? 1L : v + 1);
                uniqueUserAgents.compute(line.httpUserAgent(), (k, v) -> (v == null) ? 1L : v + 1);
            }
        });

        return LogReport.builder()
            .startingDate(startingDate)
            .endDate(endDate)
            .requestCount(count.get())
            .fileNames(fileNames)
            .responseCodes(
                responseCodes
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingLong((Entry<String, Long> entry) -> entry.getValue()).reversed())
                    .toList()
            )
            .resources(
                resources
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingLong((Entry<String, Long> entry) -> entry.getValue()).reversed())
                    .toList()
            )
            .errorRate(
                1.0 * error.get() / count.get()
            )
            .requestMethods(
                requestMethods
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingLong((Entry<String, Long> entry) -> entry.getValue()).reversed())
                    .toList()
            )
            .averageResponseByteSize(count.longValue() == 0L ? 0 : byteSum.longValue() / count.longValue())
            .uniqueUserAgents(
                uniqueUserAgents
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingLong((Entry<String, Long> entry) -> entry.getValue()).reversed())
                    .toList()
            )
            .uniqueIpAddresses(
                uniqueIpAddresses
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingLong((Entry<String, Long> entry) -> entry.getValue()).reversed())
                    .toList()
            )
            .response95pByteSize(calculate95pBytes(bytes))
            .build();
    }

    private List<String> getFileNames(String path) {
        try {
            return Files.walk(Path.of(path))
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
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
}
