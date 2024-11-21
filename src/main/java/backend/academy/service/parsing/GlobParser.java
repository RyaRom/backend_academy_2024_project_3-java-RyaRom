package backend.academy.service.parsing;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GlobParser {

    public List<Path> getNormalPaths(String file) {
        if (!isGlob(file)) {
            try {
                return Files.walk(Path.of(file))
                    .filter(Files::isRegularFile)
                    .toList();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        List<Path> matchingFiles = new ArrayList<>();
        String pattern = file.replace("\\", "/");

        int firstGlobIndex = findFirstGlobIndex(pattern);
        String rootDirPath = pattern.substring(0, firstGlobIndex);
        String globPattern = "glob:" + pattern.substring(firstGlobIndex);
        Path rootDir = rootDirPath.isEmpty() ? Paths.get(".").toAbsolutePath().normalize()
            : Paths.get(rootDirPath).toAbsolutePath().normalize();

        log.info("Root Directory: {}", rootDir);
        log.info("Glob Pattern: {}", globPattern);

        try {
            FileSystem fs = FileSystems.getDefault();
            PathMatcher matcher = fs.getPathMatcher(globPattern);

            //for non-nested files in **/... patterns
            PathMatcher matcherForDoubleAst = fs.getPathMatcher(globPattern.replace("**/", ""));
            Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path relativePath = rootDir.relativize(file);
                    log.info("Visiting file: {}", relativePath);
                    if (matcher.matches(relativePath)
                        || matcherForDoubleAst.matches(relativePath)) {
                        matchingFiles.add(file.toAbsolutePath());
                        log.debug("Matched File: {}", file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Error traversing files: {}", e.getMessage());
        }

        return matchingFiles;
    }

    public boolean isGlob(String path) {
        if (path.contains("https://") || path.contains("http://")) {
            return false;
        }
        return path.contains("*")
            || path.contains("?")
            || path.contains("{")
            || path.contains("}")
            || path.contains("[")
            || path.contains("]")
            || path.contains("**");
    }

    private int findFirstGlobIndex(String pattern) {
        String globChars = "*?[{";
        int index = -1;
        for (int i = 0; i < pattern.length(); i++) {
            if (globChars.indexOf(pattern.charAt(i)) >= 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            return pattern.lastIndexOf("/", index) + 1;
        }
        return -1;
    }
}
