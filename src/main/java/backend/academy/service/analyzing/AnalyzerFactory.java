package backend.academy.service.analyzing;

import backend.academy.data.Params;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static backend.academy.service.parsing.LogParser.FORMATTER;
import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;

@Log4j2
@RequiredArgsConstructor
public class AnalyzerFactory {
    private final Params params;

    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @SuppressWarnings("ReturnCount")
    public Analyzer getAnalyzer() {
        String field = params.filterField();
        String value = params.filterValue();
        var start = params.from() == null ? MIN : LocalDate.parse(params.from(), FORMATTER_DATE).atStartOfDay();
        var end = params.to() == null ? MAX : LocalDate.parse(params.to(), FORMATTER_DATE).atStartOfDay();
        if (field == null || value == null) {
            return new DefaultAnalyzer(
                start, end,
                (instance) -> true
            );
        }

        switch (field) {
            case "remote_addr" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.remoteAddress().equals(value)
                );
            }
            case "time_local" -> {
                LocalDateTime time = LocalDateTime.parse(value, FORMATTER);
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.timeLocal().equals(time)
                );
            }
            case "remote_user" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.remoteUser().contains(value)
                );
            }
            case "request" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.request().equals(value)
                );
            }
            case "method", "resource" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.request().contains(value)
                );
            }
            case "status" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.status().equals(value)
                );
            }
            case "body_bytes_sent" -> {
                long bytes = Long.parseLong(value);
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.bodyBitesSent() == bytes
                );
            }
            case "http_referer" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.httpRefer().contains(value)
                );
            }
            case "http_user_agent" -> {
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> instance.httpUserAgent().contains(value)
                );
            }
            default -> {
                log.error("Filter field: {} for file {} not recognized", field, params.path());
                return new DefaultAnalyzer(
                    start, end,
                    (instance) -> true
                );
            }
        }
    }
}
