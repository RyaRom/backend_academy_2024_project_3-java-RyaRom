package backend.academy.service.analyzing;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import java.util.stream.Stream;

public interface Analyzer {
    /**
     * Process lazily loaded logs from stream and return info about log file
     *
     * @param logs Stream of log lines parsed in LogInstance
     * @param path Path of dir where file is located.
     * @return LogReport with info about file
     */
    LogReport analyze(Stream<LogInstance> logs, String path);
}
