import java.util.concurrent.ThreadLocalRandom;

public class Humedad extends Sensor {
    public Humedad() {
        super("Humedad", "%", 600, 5, 50.0, 80.0);
    }

    @Override
    protected double medir() {
        return ThreadLocalRandom.current().nextDouble(40.0, 90.0);
    }
}