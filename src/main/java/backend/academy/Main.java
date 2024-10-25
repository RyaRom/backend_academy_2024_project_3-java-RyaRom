package backend.academy;

import backend.academy.data.LogReport;
import backend.academy.data.Params;
import backend.academy.service.analyzing.Analyzer;
import backend.academy.service.analyzing.DefaultAnalyzer;
import backend.academy.service.format.Formatter;
import backend.academy.service.format.FormatterFactory;
import backend.academy.service.parsing.LogParser;
import backend.academy.service.parsing.ParserFactory;
import com.beust.jcommander.JCommander;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class Main {
    public static void main(String[] args) {
        Params params = new Params();
        JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(args);

        ParserFactory parserFactory = new ParserFactory();
        FormatterFactory formatterFactory = new FormatterFactory();
        LogParser logParser = parserFactory.getParser(params.path());
        Analyzer analyzer = new DefaultAnalyzer();
        Formatter formatter = formatterFactory.getFormatter(params.format());

        var parsed = logParser.parseDir(params.path());
        LogReport report = analyzer.analyze(parsed, params.path());
        formatter.saveAndPrintReport(report);
    }
}
