package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static backend.academy.service.MainService.parseDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LogParserTest {
    private final LogParser parser = new LocalFileLogParser(new GlobParser(), StandardCharsets.UTF_8);

    @Test
    void parse() {
        var logs = new URLLogParser().parse(
                "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs")
            .flatMap(s -> s)
            .toList();
        assertNotNull(logs);
        assertEquals(51462, logs.size());
    }

    @Test
    void parseDir() {
        var logs = new ArrayList<>(parser.parse("src/test/resources/logs1").flatMap(s-> s).toList());
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
        assertEquals("93.180.71.3", instance.remoteAddress());
        assertEquals("-", instance.remoteUser());
        assertEquals(parseDateTime("17/May/2015:08:05:32 +0000"), instance.timeLocal());
        assertEquals("GET /downloads/product_1 HTTP/1.1", instance.request());
        assertEquals("304", instance.status());
        assertEquals(0L, instance.bodyBitesSent());
        assertEquals("-", instance.httpRefer());
        assertEquals("Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)", instance.httpUserAgent());
    }
}
