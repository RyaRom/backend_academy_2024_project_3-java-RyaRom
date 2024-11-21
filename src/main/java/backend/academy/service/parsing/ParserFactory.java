package backend.academy.service.parsing;

import backend.academy.data.Params;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParserFactory {
    private final GlobParser globParser = new GlobParser();
    private final Params params;

    public LogParser getParser() {
        String path = params.path();
        if (isURL(path)) {
            return new URLLogParser();
        }
        if (isLocalFile(path)) {
            return new LocalFileLogParser(globParser);
        }
        throw new IllegalArgumentException("Unsupported path: " + path);
    }

    private boolean isURL(String path) {
        try {
            new URI(path).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLocalFile(String path) {
        return globParser.isGlob(path) || Files.exists(Paths.get(path));
    }
}
