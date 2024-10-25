package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultAnalyzerTest {
    private final Stream<LogInstance> bigFileStream = Instancio.ofList(LogInstance.class)
        .size(100000)
        .create()
        .stream();

    private final Analyzer analyzer = new DefaultAnalyzer();

    @Test
    void analyzeBigFile() {
        LogReport report = analyzer.analyze(bigFileStream, "");
        assertNotNull(report);
        assertEquals(report.requestCount(), 100000);
    }
}
