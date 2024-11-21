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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public static final PrintStream CONSOLE_ERR = System.err;

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("[dd/MMM/yyyy:HH:mm:ss Z][yyyy-MM-dd]")
        .toFormatter(Locale.ENGLISH);

    private final String[] args;

    public static OffsetDateTime parseDateTime(String dateTime) {
        try {
            return OffsetDateTime.parse(dateTime, FORMATTER);
        } catch (DateTimeParseException dateTimeParseException) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, FORMATTER);
                return localDateTime.atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException dateParseException) {
                try {
                    LocalDate localDate = LocalDate.parse(dateTime, FORMATTER);
                    return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                } catch (DateTimeParseException parseException) {
                    throw new IllegalArgumentException("Unable to parse date: " + dateTime, parseException);
                }
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private static void printResult(LocalTime start, LocalTime end, Params params, String table) {
        String difference = String.format("%.2f", 1.0 * Duration.between(start, end).toMillis() / 1000);
        String path = (params.out().isBlank() ? "(no path specified)" : params.out());

        CONSOLE_WRITER.println("File created in path " + path);
        CONSOLE_WRITER.println("For " + difference + " seconds");
        CONSOLE_WRITER.println(table);
        CONSOLE_WRITER.flush();
    }

    public void run() {
        LocalTime start = LocalTime.now();

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

        LocalTime end = LocalTime.now();
        printResult(start, end, params, table);
    }
}
