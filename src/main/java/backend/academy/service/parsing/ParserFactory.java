package backend.academy.service.parsing;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserFactory {
    public LogParser getParser(String path) {
        if (isURL(path)) {
            return new URLLogParser();
        }
        if (isLocalFile(path)) {
            return new LocalFileLogParser();
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
        return Files.exists(Paths.get(path));
    }
}
