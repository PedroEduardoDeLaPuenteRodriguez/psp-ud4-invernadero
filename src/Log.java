import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Log {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private Log() { }

    public static void log(String mensaje) {
        System.out.println(LocalTime.now().format(HORA)
                + " [" + Thread.currentThread().getName() + "] " + mensaje);
    }
}