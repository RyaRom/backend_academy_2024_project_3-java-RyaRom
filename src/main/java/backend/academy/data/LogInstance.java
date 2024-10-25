package backend.academy.data;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record LogInstance(
    String remoteAddress,
    String remoteUser,
    LocalDateTime timeLocal,
    String request,
    String status,
    Long bodyBitesSent,
    String httpRefer,
    String httpUserAgent
) {

}
