package backend.academy.service.format;

import backend.academy.data.LogReport;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Formatter {
    Integer TABLE_LIMIT = 50;

    String getAndSaveTable(LogReport report);

    default String formTable(LogReport report, String tableTemplate, String columnTemplate) {
        String startingDate = String.valueOf(report.startingDate());
        String endDate = String.valueOf(report.endDate());
        if (startingDate.equals(String.valueOf(LocalDateTime.MIN))) {
            startingDate = "-";
        }
        if (endDate.equals(String.valueOf(LocalDateTime.MAX))) {
            endDate = "-";
        }
        String errorRate = String.format("%.2f", report.errorRate() * 100);

        return tableTemplate
            .replace("{fileNames}", String.join(", ", report.fileNames()))
            .replace("{startingDate}", startingDate)
            .replace("{endDate}", endDate)
            .replace("{requestCount}", String.valueOf(report.requestCount()))
            .replace("{averageResponseByteSize}", String.valueOf(report.averageResponseByteSize()))
            .replace("{response95pByteSize}", String.valueOf(report.response95pByteSize()))
            .replace("{errorRate}", errorRate)
            .replace("{uniqueIpAddresses}", formColumnsFromPairs(report.uniqueIpAddresses(), columnTemplate))
            .replace("{uniqueUserAgents}", formColumnsFromPairs(report.uniqueUserAgents(), columnTemplate))
            .replace("{resources}", formColumnsFromPairs(report.resources(), columnTemplate))
            .replace("{responseCodes}", formColumnsFromPairs(report.responseCodes(), columnTemplate))
            .replace("{requestMethods}", formColumnsFromPairs(report.requestMethods(), columnTemplate));
    }

    @SuppressWarnings("MultipleStringLiterals")
    default String formColumnsFromPairs(List<Map.Entry<String, Long>> pairs, String columnTemplate) {
        StringBuilder result = new StringBuilder();
        int size = 0;
        for (var pair : pairs) {
            size++;
            if (size > TABLE_LIMIT) {
                result.append(columnTemplate.formatted("...", "..."));
                break;
            }
            result.append(columnTemplate.formatted(pair.getKey(), pair.getValue()));
        }
        return result.toString();
    }

    default boolean saveFile(String dir, String fileName, String content) {
        File pathDir = new File(dir);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }
        File file = new File(dir + "/" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (Exception e) {
            return true;
        }
        return false;
    }
}
