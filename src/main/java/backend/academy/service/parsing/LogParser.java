package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public interface LogParser {
    DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
        .toFormatter();

    String REGEX = "^(\\S+) - (\\S+) \\[(.+?)] \"(.+?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"";
    Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Parses a log file and returns a stream of LogInstance objects lazily loaded from the file.
     *
     * @param fileName The name of the log file to parse
     * @return A stream of LogInstance objects
     */
    Stream<LogInstance> parse(String fileName);

    /**
     * Parses a log line into a LogInstance object.
     *
     * @param log The log line to parse
     * @return The parsed LogInstance object, or null if the line is invalid
     */
    @Nullable
    @SuppressWarnings("MagicNumber")
    default LogInstance mapFromString(String log) {
        try {
            if (!log.matches(REGEX)) {
                return null;
            }
            Matcher matcher = PATTERN.matcher(log);
            matcher.find();
            return LogInstance.builder()
                .remoteAddress(matcher.group(1))
                .remoteUser(matcher.group(2))
                .timeLocal(LocalDateTime.parse(matcher.group(3), FORMATTER))
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
