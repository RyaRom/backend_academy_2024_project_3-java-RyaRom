package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.util.stream.Stream;

public interface Analyzer {
    LogReport analyze(Stream<LogInstance> logs);
}
