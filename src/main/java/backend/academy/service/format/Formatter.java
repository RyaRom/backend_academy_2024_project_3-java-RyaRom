package backend.academy.service.format;

import backend.academy.data.LogReport;

public interface Formatter {
    String getTable(LogReport report);
}
