package backend.academy.service;

import backend.academy.data.LogInstance;
import backend.academy.data.LogReport;
import backend.academy.data.Params;
import backend.academy.service.analyzing.Analyzer;
import backend.academy.service.analyzing.AnalyzerFactory;
import backend.academy.service.format.Formatter;
import backend.academy.service.format.FormatterFactory;
import backend.academy.service.parsing.LogParser;
import backend.academy.service.parsing.ParserFactory;
import com.beust.jcommander.JCommander;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MainService {
    public static final PrintStream CONSOLE_WRITER = System.out;
    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("[dd/MMM/yyyy:HH:mm:ss Z][yyyy-MM-dd]")
        .toFormatter(Locale.ENGLISH);

    private final String[] args;

    public static OffsetDateTime parseDateTime(String dateTime) {
        try {
            return OffsetDateTime.parse(dateTime, FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, FORMATTER);
                return localDateTime.atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException e1) {
                try {
                    LocalDate localDate = LocalDate.parse(dateTime, FORMATTER);
                    return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                } catch (DateTimeParseException e2) {
                    throw new IllegalArgumentException("Unable to parse date: " + dateTime, e2);
                }
            }
        }
    }

    public void run() {
        Params params = new Params();
        JCommander jc = JCommander.newBuilder()
            .addObject(params)
            .build();

        jc.parse(args);
        if (params.help()) {
            jc.usage();
            return;
        }

        LogParser logParser = new ParserFactory(params).getParser();
        Formatter formatter = new FormatterFactory(params).getFormatter();
        Analyzer analyzer = new AnalyzerFactory(params).getAnalyzer();

        Stream<LogInstance> parsed = logParser.parse(params.path());
        LogReport report = analyzer.analyze(parsed, params.path());
        String table = formatter.getAndSaveTable(report);

        CONSOLE_WRITER.println(table);
        CONSOLE_WRITER.flush();
    }
}
