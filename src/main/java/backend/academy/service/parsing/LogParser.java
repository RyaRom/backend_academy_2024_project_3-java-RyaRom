package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import static backend.academy.service.MainService.parseDateTime;

public interface LogParser {
    String REGEX = "^(\\S+) - (\\S+) \\[(.+?)] \"(.+?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"";

    Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Parses a log file and returns a stream of LogInstance objects lazily loaded from the file.
     *
     * @param fileName The name of the log file to parse
     * @return A stream of LogInstance objects
     */
    Stream<Stream<LogInstance>> parse(String fileName);

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
                .timeLocal(parseDateTime(matcher.group(3)))
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
