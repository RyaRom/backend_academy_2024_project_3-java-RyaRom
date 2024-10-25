package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LogParserTest {
    private final LogParser parser = new LocalFileLogParser();

    @Test
    void parse() {
    }

    @Test
    void parseDir() {
    }

    @Test
    void mapFromString() {
        String example =
            "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";
        LogInstance instance = parser.mapFromString(example);
        assertEquals(instance.remoteAddress(), "93.180.71.3");
        assertEquals(instance.remoteUser(), "-");
        assertEquals(instance.timeLocal(),
            LocalDateTime.parse("17/May/2015:08:05:32 +0000", new DateTimeFormatterBuilder()
                .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
                .toFormatter()));
        assertEquals(instance.request(), "GET /downloads/product_1 HTTP/1.1");
        assertEquals(instance.status(), "304");
        assertEquals(instance.bodyBitesSent(), 0L);
        assertEquals(instance.httpRefer(), "-");
        assertEquals(instance.httpUserAgent(), "Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)");
    }
}
