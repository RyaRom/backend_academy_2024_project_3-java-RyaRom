package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LocalFileLogParser implements LogParser {
    @Override
    public Stream<LogInstance> parse(String fileName) {
        Path dir = Path.of(fileName);
        if (Files.isRegularFile(dir)) {
            return parseFile(fileName);
        }
        try {
            return Files.walk(dir)
                .parallel()
                .filter(Files::isRegularFile)
                .flatMap(path -> parseFile(path.toString()));
        } catch (IOException e) {
            log.error("Error while reading dir {}", fileName, e);
            return Stream.empty();
        }
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
