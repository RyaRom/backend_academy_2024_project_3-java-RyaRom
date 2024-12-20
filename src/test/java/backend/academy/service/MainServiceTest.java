package backend.academy.service;

import com.beust.jcommander.ParameterException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainServiceTest {
    private static Stream<Arguments> provideValidArguments() {
        return Stream.of(
            Arguments.of((Object) new String[] {
                "-p",
                "src/test/resources/datedLogs.txt",
                "--from",
                "2010-08-31",
                "--to",
                "2025-08-31",
                "-f",
                "adoc",
                "-o",
                "src/main/resources"}),
            Arguments.of((Object) new String[] {
                "-p",
                "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs",
                "--from",
                "21/Nov/2010:15:42:23 +0000",
                "--to",
                "2025-08-31",
                "-f",
                "markdown",
                "-o",
                "src/main/resources"}),
            Arguments.of((Object) new String[] {
                "-p",
                "src/test/resources/datedLogs.txt",
                "-f",
                "markdown",
                "-o",
                "src/main/resources"}),
            Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources/logs1",
                    "-f",
                    "adoc",
                    "-o",
                    "src/main/resources"}),
            Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources/datedLogs.txt",
                    "-f",
                    "markdown",
                    "--filter-field",
                    "remote_addr",
                    "-o",
                    "src/main/resources"}
            ),
            Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources/datedLogs.txt",
                    "-f",
                    "markdown",
                    "--filter-value",
                    "80.91.33.133",
                    "-o",
                    "src/main/resources"}
            ),
            Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources/datedLogs.txt",
                    "-f",
                    "markdown",
                    "--filter-field",
                    "not_recognized",
                    "--filter-value",
                    "80.91.33.133",
                    "-o",
                    "src/main/resources"}
            ),
            Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources/datedLogs.txt",
                    "-f",
                    "markdown",
                    "--filter-field",
                    "remote_addr",
                    "--filter-value",
                    "80.91.33.133",
                    "-o",
                    "src/main/resources"}
            ), Arguments.of(
                (Object) new String[] {
                    "-p",
                    "src/test/resources",
                    "-f",
                    "markdown",
                    "-e",
                    "UTF-8",
                    "-o",
                    "src/main/resources",
                }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidArguments")
    void testValidArguments(String[] args) {
        MainService mainService = new MainService(args);
        assertThatCode(mainService::run).doesNotThrowAnyException();
        assertTrue(Files.exists(Path.of(args[args.length - 1])));
    }

    @Test
    void testMissingPathArgument() {
        String[] args = new String[] {"-f", "markdown", "-o", ""};
        MainService mainService = new MainService(args);
        assertThrows(ParameterException.class, mainService::run);
    }
}
