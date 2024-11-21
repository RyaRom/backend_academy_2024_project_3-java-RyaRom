package backend.academy.data;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@SuppressWarnings("LineLength")
public final class Params {
    @Parameter(required = true, names = {"--path", "-p"}, description = "The path to the input file or directory.")
    private String path;

    @Parameter(names = {"--from"}, description = "The start date for filtering logs (format: yyyy-MM-dd).")
    private String from;

    @Parameter(names = {"--to"}, description = "The end date for filtering logs (format: yyyy-MM-dd).")
    private String to;

    @Parameter(names = {"--format", "-f"}, description = "The output format (default: markdown).")
    private String format = "markdown";

    /**
     * A path where report doc will be saved
     */
    @Parameter(names = {"--out", "-o"}, description = "The output path where the generated report will be saved.")
    private String out;

    /**
     * A field to filter by in log entries. The following field
     * names are supported, along with their matching behavior:
     *
     * <ul>
     *     <li><b>remote_addr</b>: Filters by the remote IP address is equals to {@code --filter-value} .</li>
     *     <li><b>time_local</b>: Filters by the local time of the request is equals to {@code --filter-value} .</li>
     *     <li><b>remote_user</b>: Filters by the remote user associated with the request is containing the {@code --filter-value} .</li>
     *     <li><b>request</b>: Filters by the entire request string is equals to {@code --filter-value} .</li>
     *     <li><b>method</b>: Filters by the HTTP method is containing the {@code --filter-value} .</li>
     *     <li><b>resource</b>: Filters by the resource accessed in the request is containing the {@code --filter-value} .</li>
     *     <li><b>status</b>: Filters by the HTTP response status code is equals to {@code --filter-value} .</li>
     *     <li><b>body_bytes_sent</b>: Filters by the size of the response body in bytes is equals to {@code --filter-value} .</li>
     *     <li><b>http_referer</b>: Filters by the HTTP referrer is containing the {@code --filter-value} .</li>
     *     <li><b>http_user_agent</b>: Filters by the HTTP user agent is containing the {@code --filter-value} .</li>
     * </ul>
     */
    @Parameter(names = {
        "--filter-field"}, description = "The log entry field to filter by. See below for supported fields.")
    private String filterField;

    /**
     * A value to filter by
     */
    @Parameter(names = {"--filter-value"}, description = "The value to filter the specified field by.")
    private String filterValue;

    @Parameter(names = {"--help", "-h"}, help = true, description = "Displays this help message.")
    private boolean help;
}
