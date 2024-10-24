package backend.academy.service.parsing;

import backend.academy.data.LogInstance;
import java.util.List;

public interface Parser {
    List<LogInstance> parse(String fileName);
}
