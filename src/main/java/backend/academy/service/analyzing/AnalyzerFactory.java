package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.Params;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static backend.academy.service.MainService.parseDateTime;

@Log4j2
@RequiredArgsConstructor
public class AnalyzerFactory {
    private final Params params;

    public Analyzer getAnalyzer() {
        String field = params.filterField();
        String value = params.filterValue();
        var start = params.from() == null ? OffsetDateTime.MIN : parseDateTime(params.from());
        var end = params.to() == null ? OffsetDateTime.MAX : parseDateTime(params.to());
        if (value == null || field == null) {
            log.warn("Filter value is null. Default analyzer will be used");
            return new DefaultAnalyzer(start, end, instance -> true, Charset.forName(params.encoding()));
        }

        Predicate<LogInstance> filter = switch (field) {
            case "remote_addr" -> instance -> instance.remoteAddress().equals(value);

            case "time_local" -> instance -> instance.timeLocal().isEqual(parseDateTime(value));

            case "remote_user" -> instance -> instance.remoteUser().contains(value);

            case "request" -> instance -> instance.request().equals(value);

            case "method", "resource" -> instance -> instance.request().contains(value);

            case "status" -> instance -> instance.status().equals(value);

            case "body_bytes_sent" -> instance -> instance.bodyBitesSent() == Long.parseLong(value);

            case "http_referer" -> instance -> instance.httpRefer().contains(value);

            case "http_user_agent" -> instance -> instance.httpUserAgent().contains(value);

            default -> instance -> true;
        };
        return new DefaultAnalyzer(start, end, filter, Charset.forName(params.encoding()));
    }
}
