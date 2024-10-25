package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.nio.file.Path;
import java.util.stream.Stream;

public class URLLogParser implements LogParser {
    @Override
    public Stream<LogInstance> parse(Path fileName) {
        return Stream.of();
    }

    @Override
    public Stream<LogInstance> parseDir(String dirName) {
        return Stream.empty();
    }
}
