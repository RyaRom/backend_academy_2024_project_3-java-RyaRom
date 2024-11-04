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
    @Parameter(required = true, names = {"--path", "-p"})
    private String path;

    @Parameter(names = {"--from"})
    private String from;

    @Parameter(names = {"--to"})
    private String to;

    @Parameter(names = {"--format", "-f"})
    private String format = "adoc";

    /**
     * A path where report doc will be saved
     */
    @Parameter(names = {"--out", "-o"})
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
    @Parameter(names = {"--filter-field"})
    private String filterField;

    /**
     * A value to filter by
     */
    @Parameter(names = {"--filter-value"})
    private String filterValue;
}
