package backend.academy.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MainServiceTest {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources/testLogs, '2010-08-31', '2025-08-31', markdown, C:\\Users\\locadm\\Documents\\ProjectFiles"
    })
    void allParams(String path, String from, String to, String format, String out) {
        String[] args = new String[] {"-p", path, "--from", from, "--to", to, "-f", format, "-o", out};
        MainService mainService = new MainService(args);
        mainService.run();
    }
}
