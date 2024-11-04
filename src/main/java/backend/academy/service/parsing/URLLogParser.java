package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class URLLogParser implements LogParser {
    @Override
    public Stream<LogInstance> parse(String fileName) {
        try {
            URL url = new URI(fileName).toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            return reader.lines()
                .map(this::mapFromString)
                .filter(Objects::nonNull)
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.error("Error closing the BufferedReader for URL {}", url, e);
                    }
                });
        } catch (Exception e) {
            log.error("Error while reading URL {}", e.getMessage());
            return Stream.empty();
        }
    }

}
