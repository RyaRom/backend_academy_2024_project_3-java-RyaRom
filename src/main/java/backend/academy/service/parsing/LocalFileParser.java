package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import backend.academy.exception.FileParsingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LocalFileParser implements Parser {
    @Override
    public Stream<LogInstance> parse(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.lines()
                .map(this::mapFromString)
                .filter(Objects::nonNull);
        } catch (IOException e) {
            log.error("Error while reading file {}", fileName, e);
            throw new FileParsingException();
        }
    }
}
