package backend.academy.service;

import java.nio.file.Files;
import java.nio.file.Path;
import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainServiceTest {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/datedLogs, '2010-08-31', '2025-08-31', adoc, src/main/resources",
        "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs, '2010-08-31', '2025-08-31', markdown, src/main/resources"
    })
    void allParams(String path, String from, String to, String format, String out) {
        String[] args = new String[] {"-p", path, "--from", from, "--to", to, "-f", format, "-o", out};
        MainService mainService = new MainService(args);
        assertThatCode(mainService::run).doesNotThrowAnyException();
        assertTrue(Files.exists(Path.of(out)));
    }

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/datedLogs, markdown, src/main/resources",
        "src/test/resources/logs1, adoc, src/main/resources"
    })
    void noDate(String path, String format, String out) {
        String[] args = new String[] {"-p", path, "-f", format, "-o", out};
        MainService mainService = new MainService(args);
        assertThatCode(mainService::run).doesNotThrowAnyException();
        assertTrue(Files.exists(Path.of(out)));
    }

    @Test
    void noPath() {
        String[] args = new String[] {"-f", "markdown", "-o", "src/main/resources"};
        MainService mainService = new MainService(args);
        assertThrows(ParameterException.class, mainService::run);
    }
}
