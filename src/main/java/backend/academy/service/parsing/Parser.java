package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface Parser {
    String regex =
        "^(?<address>\\S+) - (?<user>\\S+) \\[(?<time>[^]]+)] \"(?<request>[^\"]+)\" " +
            "(?<status>\\d{3}) (?<bytesSent>\\d+) \"(?<httpRefer>[^\"]*)\" \"(?<httpUserAgent>[^\"]*)\"";
    Pattern pattern = Pattern.compile(regex);

    Stream<LogInstance> parse(String fileName);

    default LogInstance mapFromString(String log) {
        Matcher matcher = pattern.matcher(log);
        if (!log.matches(regex)) {
            return null;
        }
        return LogInstance.builder()
            .remoteAddress(matcher.group("address"))
            .remoteUser(matcher.group("user"))
            .timeLocal(LocalDateTime.parse(matcher.group("time"), DateTimeFormatter.ISO_OFFSET_DATE))
            .request(matcher.group("request"))
            .status(Integer.parseInt(matcher.group("status")))
            .bodyBitesSent(Long.parseLong(matcher.group("bytesSent")))
            .httpRefer(matcher.group("httpRefer"))
            .httpUserAgent(matcher.group("httpUserAgent"))
            .build();
    }
}
