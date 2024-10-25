package backend.academy;

import backend.academy.data.Params;
import com.beust.jcommander.JCommander;
import java.util.PriorityQueue;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class Main {
    public static void main(String[] args) {
        Params params = new Params();
        JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(args);
    }
}
