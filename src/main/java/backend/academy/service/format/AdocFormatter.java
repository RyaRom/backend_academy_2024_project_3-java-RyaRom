package backend.academy.service.format;

import backend.academy.data.LogReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class AdocFormatter implements Formatter {
    private final String out;

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
    public String getAndSaveTable(LogReport report) {
        String table = formTable(report, MAIN_TABLE_TEMPLATE, COLUMN_TEMPLATE);
        log.info("Report processed:\n {}", table);
        if (saveFile(out, "log_report.adoc", table)) {
            log.error("Error writing report to file");
        }
        return table;
    }
}
