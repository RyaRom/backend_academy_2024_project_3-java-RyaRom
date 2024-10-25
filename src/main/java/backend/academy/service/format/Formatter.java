package backend.academy.service.format;

import backend.academy.data.LogReport;

public interface Formatter {
    void saveAndPrintReport(LogReport report);
}
