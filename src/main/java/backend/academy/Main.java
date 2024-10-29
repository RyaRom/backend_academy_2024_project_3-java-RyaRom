package backend.academy;

import backend.academy.service.MainService;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class Main {
    public static void main(String[] args) {
        MainService mainService = new MainService(args);
        mainService.run();
    }
}
