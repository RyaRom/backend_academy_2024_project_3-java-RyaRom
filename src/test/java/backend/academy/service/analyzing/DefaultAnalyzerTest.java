package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import backend.academy.data.Params;
import backend.academy.service.parsing.GlobParser;
import backend.academy.service.parsing.LocalFileLogParser;
import backend.academy.service.parsing.LogParser;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static java.time.OffsetDateTime.MAX;
import static java.time.OffsetDateTime.MIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultAnalyzerTest {
    private final Stream<LogInstance> bigFileStream = Instancio.ofList(LogInstance.class)
        .size(100000)
        .create()
        .stream();

    private final LogParser parser = new LocalFileLogParser(new GlobParser());

    private static Stream<Arguments> provideParamsForFilters() {
        return Stream.of(
            Arguments.of("remote_addr", "80.91.33.133", 1),
            Arguments.of("time_local", "18/May/2015:08:05:23 +0000", 1),
            Arguments.of("remote_user", "user123", 0),
            Arguments.of("request", "GET /downloads/product_1 HTTP/1.1", 1),
            Arguments.of("method", "GET", 4),
            Arguments.of("resource", "/product_2", 2),
            Arguments.of("status", "404", 3),
            Arguments.of("body_bytes_sent", "339", 1),
            Arguments.of("http_referer", "-", 4),
            Arguments.of("http_user_agent", "Debian", 4)
        );
    }

    @Test
    void analyzeBigRandomFile() {
        Analyzer analyzer = new DefaultAnalyzer(MIN, MAX, _ -> true);
        LogReport report = analyzer.analyze(bigFileStream, "");
        assertNotNull(report);
        assertEquals( 100000,report.requestCount());
    }

    @Test
    void datedLogs() {
        Analyzer analyzer = new DefaultAnalyzer(
            OffsetDateTime.of(LocalDateTime.of(2015, 5, 18, 0, 0, 0), ZoneOffset.ofTotalSeconds(0)),
            OffsetDateTime.of(LocalDateTime.of(2015, 5, 20, 0, 0, 0), ZoneOffset.ofTotalSeconds(0)),
            _ -> true
        );
        var logs = parser.parse("src/test/resources/datedLogs.txt");
        LogReport report = analyzer.analyze(logs, "src/test/resources/logs1");
        System.out.println(report);
        assertNotNull(report);
        assertEquals(2, report.requestCount());
    }

    @ParameterizedTest
    @MethodSource("provideParamsForFilters")
    void filterLogs(String filterField, String filterValue, int expectedCount) {
        Params params = new Params();
        params.path("src/test/resources/datedLogs.txt");
        params.filterField(filterField);
        params.filterValue(filterValue);

        Analyzer analyzer = new AnalyzerFactory(params).getAnalyzer();
        var logs = parser.parse("src/test/resources/datedLogs.txt");
        LogReport report = analyzer.analyze(logs, "src/test/resources/logs1");

        System.out.println(report);
        assertNotNull(report);
        assertEquals(expectedCount, report.requestCount());
    }
}
