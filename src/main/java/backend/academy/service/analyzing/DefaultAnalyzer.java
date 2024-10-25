package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.util.stream.Stream;

public class DefaultAnalyzer implements Analyzer {
    @Override
    public LogReport analyze(Stream<LogInstance> logs) {
    }
}
