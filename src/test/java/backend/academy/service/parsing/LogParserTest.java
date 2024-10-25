package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LogParserTest {
    private final LogParser parser = new LocalFileLogParser();

    @Test
    void parse() {
        List<LogInstance> logs = parser.parse(Path.of("src/test/resources/testLogs")).toList();

        assertNotNull(logs);
        assertEquals(51462, logs.size());
    }

    @Test
    void parseDir() {
        List<LogInstance> logs = new ArrayList<>(parser.parseDir("src/test/resources/logs1").toList());
        List<LogInstance> correct = new ArrayList<>(List.of(
            parser.mapFromString(
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\""),
            parser.mapFromString(
                "93.180.71.3 - - [17/May/2015:08:05:23 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\""),
            parser.mapFromString(
                "80.91.33.133 - - [17/May/2015:08:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\""),
            parser.mapFromString(
                "217.168.17.5 - - [17/May/2015:08:05:34 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.10.3)\"")
        ));
        correct.sort(Comparator.comparing(LogInstance::remoteAddress));
        logs.sort(Comparator.comparing(LogInstance::remoteAddress));

        assertNotNull(logs);
        assertEquals(4, logs.size());
        assertEquals(logs, correct);
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
