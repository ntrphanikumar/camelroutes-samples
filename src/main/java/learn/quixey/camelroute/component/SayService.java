package learn.quixey.camelroute.component;

public class SayService {
    private final String message;

    public SayService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}