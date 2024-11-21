package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LogParserTest {
    private final LogParser parser = new LocalFileLogParser(new GlobParser());

    @Test
    void parse() {
        List<LogInstance> logs = new URLLogParser().parse(
                "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs")
            .toList();
        assertNotNull(logs);
        assertEquals(51462, logs.size());
    }

    @Test
    void parseDir() {
        List<LogInstance> logs = new ArrayList<>(parser.parse("src/test/resources/logs1").toList());
        List<LogInstance> correct = new ArrayList<>(List.of(
            Objects.requireNonNull(parser.mapFromString(
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"")),
            Objects.requireNonNull(parser.mapFromString(
                "93.180.71.3 - - [17/May/2015:08:05:23 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"")),
            Objects.requireNonNull(parser.mapFromString(
                "80.91.33.133 - - [17/May/2015:08:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\"")),
            Objects.requireNonNull(parser.mapFromString(
                "217.168.17.5 - - [17/May/2015:08:05:34 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.10.3)\""))
        ));
        assertNotNull(logs);
        assertEquals(4, logs.size());
        assertThat(logs).containsExactlyInAnyOrderElementsOf(correct);
    }

    @ParameterizedTest
    @CsvSource({
        "src\\test\\resources\\datedLogs.txt, 1",
        "src\\test\\resources\\logs1\\log3.txt, 1",
        "src\\test\\resources\\logs1\\*.txt, 1",
        "src\\test\\resources\\logs1\\logs2\\*.txt, 2",
        "src/test/resources/logs1/logs2/log*.txt, 2",
        "src\\test\\resources\\logs1\\logs2\\**\\*.txt, 3",
        "src\\test\\resources\\logs1\\logs2\\**\\*.{txt}, 3",
        ".\\src\\test\\resources\\logs1\\**\\*.txt, 4",
        "src\\test\\resources\\logs1\\logs2\\logs3\\*.txt, 1",
        ".\\src\\test\\resources\\logs1\\**\\log?.txt, 4",
        ".\\src\\test\\resources\\logs1\\**\\log???.txt, 0",
    })
    void wildcardPath(String path, int expectedSize) {
        var globPathFiles = new GlobParser().getNormalPaths(path);
        System.out.println(globPathFiles);
        assertEquals(expectedSize, globPathFiles.size());
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
