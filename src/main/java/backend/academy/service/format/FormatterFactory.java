package backend.academy.service.format;

public class FormatterFactory {
    public Formatter getFormatter(String format) {
        if (format.equalsIgnoreCase("adoc")) {
            return new AdocFormatter();
        } else if (format.equalsIgnoreCase("markdown")) {
            return new MarkdownFormatter();
        }
        throw new IllegalArgumentException("Unknown format: " + format);
    }
}
