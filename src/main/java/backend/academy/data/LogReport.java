package backend.academy.data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map.Entry;
import lombok.Builder;

@Builder
@SuppressWarnings("RecordComponentNumber")
public record LogReport(

    OffsetDateTime startingDate,

    OffsetDateTime endDate,

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
