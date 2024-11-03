package backend.academy.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Builder;

@Builder
public record LogReport(

    LocalDateTime startingDate,

    LocalDateTime endDate,

    Long requestCount,

    Long averageResponseByteSize,

    Long response95pByteSize,

    Double errorRate,

    List<String> fileNames,

    List<Entry<String, Long>> resources,

    List<Entry<String, Long>> uniqueIpAddresses,

    List<Entry<String, Long>> uniqueUserAgents,

    List<Entry<String, Long>> responseCodes,

    List<Entry<String, Long>> requestMethods
) {
}
