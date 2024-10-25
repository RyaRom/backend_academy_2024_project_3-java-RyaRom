package backend.academy.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class LogReport {
    private Long requestCount;

    private Long averageResponseByteSize;

    private Long response95pByteSize;

    private Double errorRate;

    private List<String> fileNames;

    private Map<String, Long> resources;

    private Set<String> uniqueIpAddresses;

    private Set<String> uniqueUserAgents;

    private Map<String, Long> responseCodes;

    private Map<String, Long> requestMethods;
}
