import server.Start;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("polyglot.js.nashorn-compat", "true");
        Start.instance.run(true);
    }
}
