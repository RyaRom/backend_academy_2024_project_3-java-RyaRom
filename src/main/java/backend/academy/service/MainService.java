package backend.academy.service;

import backend.academy.data.LogReport;
import backend.academy.data.Params;
import backend.academy.service.analyzing.Analyzer;
import backend.academy.service.analyzing.DefaultAnalyzer;
import backend.academy.service.format.Formatter;
import backend.academy.service.format.FormatterFactory;
import backend.academy.service.parsing.LogParser;
import backend.academy.service.parsing.ParserFactory;
import com.beust.jcommander.JCommander;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;

@RequiredArgsConstructor
public final class MainService {

    public final static PrintStream CONSOLE_WRITER = System.out;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String[] args;

    public void run() {
        Params params = new Params();
        JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(args);

        LogParser logParser = new ParserFactory(params).getParser();
        Formatter formatter = new FormatterFactory(params).getFormatter();
        Analyzer analyzer = new DefaultAnalyzer(
            params.from() == null ? MIN : LocalDate.parse(params.from(), FORMATTER).atStartOfDay(),
            params.to() == null ? MAX : LocalDate.parse(params.to(), FORMATTER).atStartOfDay()
        );

        var parsed = logParser.parse(params.path());
        LogReport report = analyzer.analyze(parsed, params.path());
        String table = formatter.getAndSaveTable(report);

        CONSOLE_WRITER.println(table);
        CONSOLE_WRITER.flush();
    }
}
