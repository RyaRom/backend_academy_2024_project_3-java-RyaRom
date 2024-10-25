package backend.academy.data;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public final class LogReport {
    private Long requestCount;

    private Long averageResponseByteSize;

    private Long response95pByteSize;

    private Double errorRate;

    private List<String> fileNames;

    private List<Entry<String, Long>> resources;

    private Set<String> uniqueIpAddresses;

    private Set<String> uniqueUserAgents;

    private List<Entry<String, Long>> responseCodes;

    private List<Entry<String, Long>> requestMethods;
}
