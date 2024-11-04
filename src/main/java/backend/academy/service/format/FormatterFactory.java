package backend.academy.service.format;

import backend.academy.data.Params;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormatterFactory {
    private final Params params;

    public Formatter getFormatter() {
        String format = params.format();
        if (format.equalsIgnoreCase("adoc")) {
            return new AdocFormatter(params.out());
        } else if (format.equalsIgnoreCase("markdown")) {
            return new MarkdownFormatter(params.out());
        }
        throw new IllegalArgumentException("Unknown format: " + format);
    }
}
