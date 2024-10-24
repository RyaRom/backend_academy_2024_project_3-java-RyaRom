package backend.academy.data;

import java.time.LocalDateTime;

public record LogInstance(
    String remoteAddress,
    String remoteUser,
    LocalDateTime timeLocal,
    String request,
    Integer status,
    Long bodyBitesSent,
    String httpRefer,
    String httpUserAgent
) {
}
