package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class LocalFileLogParser implements LogParser {
    private final GlobParser globParser;

    @Override
    public Stream<LogInstance> parse(String fileName) {
        List<Path> paths = globParser.getNormalPaths(fileName);
        return paths.stream()
            .parallel()
            .filter(Files::isRegularFile)
            .flatMap(path -> parseFile(path.toString()));
    }

    private Stream<LogInstance> parseFile(String fileName) {
        return Stream.of(fileName).flatMap(path -> {
            try {
                return Files.lines(Path.of(path))
                    .parallel()
                    .map(this::mapFromString)
                    .filter(Objects::nonNull);
            } catch (IOException e) {
                log.error("Error while reading file {}", fileName, e);
                return Stream.empty();
            }
        });
    }
}
