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
    public Stream<LogInstance> parse(Path fileName) {
        try (Stream<String> lines = Files.lines(fileName)) {
            return lines
                .map(this::mapFromString)
                .filter(Objects::nonNull);
        } catch (IOException e) {
            log.error("Error while reading file {}", fileName, e);
            return Stream.empty();
        }
    }

    @Override
    public Stream<LogInstance> parseDir(String dirName) {
        Path dir = Path.of(dirName);
        if (Files.isRegularFile(dir)) {
            return parse(dir);
        }
        try (Stream<Path> inside = Files.walk(dir)) {
            return inside
                .filter(Files::isRegularFile)
                .flatMap(this::parse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
