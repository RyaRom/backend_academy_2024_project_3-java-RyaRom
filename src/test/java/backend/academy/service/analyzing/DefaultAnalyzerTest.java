package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import backend.academy.service.parsing.LocalFileLogParser;
import backend.academy.service.parsing.LogParser;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultAnalyzerTest {
    private final Stream<LogInstance> bigFileStream = Instancio.ofList(LogInstance.class)
        .size(100000)
        .create()
        .stream();

    private final LogParser parser = new LocalFileLogParser();

    private Analyzer analyzer = new DefaultAnalyzer(MIN, MAX);

    @Test
    void analyzeBigRandomFile() {
        LogReport report = analyzer.analyze(bigFileStream, "");
        assertNotNull(report);
        assertEquals(report.requestCount(), 100000);
    }

    @Test
    void datedLogs() {
        analyzer = new DefaultAnalyzer(
            LocalDateTime.of(2015, 5, 18, 0, 0, 0),
            LocalDateTime.of(2015, 5, 20, 0, 0, 0)
        );
        var logs = parser.parse("src/test/resources/datedLogs");
        LogReport report = analyzer.analyze(logs, "src/test/resources/logs1");
        System.out.println(report);
        assertNotNull(report);
        assertEquals(report.requestCount(), 2);
    }
}
