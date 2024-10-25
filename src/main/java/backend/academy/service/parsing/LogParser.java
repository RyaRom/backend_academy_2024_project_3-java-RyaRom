package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public interface LogParser {
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
        .toFormatter();

    String regex = "^(\\S+) - (\\S+) \\[(.+?)] \"(.+?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"";
    Pattern pattern = Pattern.compile(regex);

    Stream<LogInstance> parse(Path fileName);

    Stream<LogInstance> parseDir(String dirName);

    @Nullable
    default LogInstance mapFromString(String log) {
        try {
            if (!log.matches(regex)) {
                return null;
            }
            Matcher matcher = pattern.matcher(log);
            matcher.find();
            return LogInstance.builder()
                .remoteAddress(matcher.group(1))
                .remoteUser(matcher.group(2))
                .timeLocal(LocalDateTime.parse(matcher.group(3), formatter))
                .request(matcher.group(4))
                .status(matcher.group(5))
                .bodyBitesSent(Long.parseLong(matcher.group(6)))
                .httpRefer(matcher.group(7))
                .httpUserAgent(matcher.group(8))
                .build();
        } catch (Exception e) {
            return null;
        }
    }
}
