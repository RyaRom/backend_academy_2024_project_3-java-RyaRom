package backend.academy.data;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class Params {
    @Parameter(required = true, names = {"--path", "-p"})
    private String path;

    @Parameter(names = {"--from"})
    private String from;

    @Parameter(names = {"--to"})
    private String to;

    @Parameter(names = {"--format", "-f"})
    private String format = "adoc";
}
