package backend.academy.service.format;

import backend.academy.data.LogReport;
import java.util.List;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AdocFormatter implements Formatter {
    private static final String COLUMN_TEMPLATE = """
        |%s|%s

        """;

    private static final String MAIN_TABLE_TEMPLATE =
        """
            = Log Report Analysis

            [cols="2,4,3", options="header"]
            |===
            | Field | Value | Description

            | Files analyzed
            | {fileNames}
            | List of file names analyzed in the log report.

            | Starting Date
            | {startingDate}
            | The start date and time of the report.

            | End Date
            | {endDate}
            | The end date and time of the report.

            | Request Count
            | {requestCount}
            | Total number of requests processed.

            | Average Response Byte Size
            | {averageResponseByteSize}
            | The average size of the response payloads in bytes.

            | 95th Percentile Response Byte Size
            | {response95pByteSize}
            | The 95th percentile size of the response payloads in bytes.

            | Error Rate
            | {errorRate}%
            | The error rate as a percentage of total requests.

            |===

            = Resources requested
            === List of resources requested and their respective counts.
            [cols="2,4", options="header"]
            |===
            | Resource | Number
            {resources}
            |===

            = Response codes
            === List of response codes and their respective counts.

            [cols="2,4", options="header"]
            |===
            | Code | Number
            {responseCodes}
            |===

            = Request methods
            === List of HTTP methods (GET, POST, etc.) and their respective counts.

            [cols="2,4", options="header"]
            |===
            | Method | Number
            {requestMethods}
            |===

            = Unique IP Addresses
            === Table of most frequent IP addresses that accessed the system.

            [cols="2,4", options="header"]
            |===
            | IP | Number
            {uniqueIpAddresses}
            |===

            = Unique User Agents
            === Table of most frequent user agents used by clients.

            [cols="2,4", options="header"]
            |===
            | Agent | Number
            {uniqueUserAgents}
            |===

            """;

    @Override
    public String getTable(LogReport report) {
        String table = formTable(report);
        log.info("Report processed:\n {}", table);
        return table;
    }

    private String formTable(LogReport report) {
        return MAIN_TABLE_TEMPLATE
            .replace("{fileNames}", String.join(" ", report.fileNames()))
            .replace("{startingDate}", report.startingDate().toString())
            .replace("{endDate}", report.endDate().toString())
            .replace("{requestCount}", String.valueOf(report.requestCount()))
            .replace("{averageResponseByteSize}", String.valueOf(report.averageResponseByteSize()))
            .replace("{response95pByteSize}", String.valueOf(report.response95pByteSize()))
            .replace("{errorRate}", String.valueOf(report.errorRate()))
            .replace("{uniqueIpAddresses}", formColumnsFromPairs(report.uniqueIpAddresses()))
            .replace("{uniqueUserAgents}", formColumnsFromPairs(report.uniqueUserAgents()))
            .replace("{resources}", formColumnsFromPairs(report.resources()))
            .replace("{responseCodes}", formColumnsFromPairs(report.responseCodes()))
            .replace("{requestMethods}", formColumnsFromPairs(report.requestMethods()));
    }

    private String formColumnsFromPairs(List<Entry<String, Long>> pairs) {
        StringBuilder result = new StringBuilder();
        int size = 0;
        for (var pair : pairs) {
            size++;
            if (size > 50) {
                result.append(COLUMN_TEMPLATE.formatted("...", "..."));
                break;
            }
            result.append(COLUMN_TEMPLATE.formatted(pair.getKey(), pair.getValue()));
        }
        return result.toString();
    }
}
