package backend.academy.data;

import lombok.Builder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Builder
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
