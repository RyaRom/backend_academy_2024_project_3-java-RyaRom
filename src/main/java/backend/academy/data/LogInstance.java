package backend.academy.data;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record LogInstance(
    String remoteAddress,
    String remoteUser,
    OffsetDateTime timeLocal,
    String request,
    String status,
    Long bodyBitesSent,
    String httpRefer,
    String httpUserAgent
) {

}
