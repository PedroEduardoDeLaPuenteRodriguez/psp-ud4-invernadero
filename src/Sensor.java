public abstract class Sensor implements Runnable {
    private final String nombre;
    private final String unidad;
    private final long periodoMs;
    private final int numLecturas;
    private final double umbralMin;
    private final double umbralMax;

    // Estadísticas propias de cada sensor
    private volatile int lecturas = 0;
    private volatile int alertas = 0;
    private volatile double suma = 0;
    private volatile double minimo = Double.MAX_VALUE;
    private volatile double maximo = -Double.MAX_VALUE;

    protected Sensor(String nombre, String unidad, long periodoMs, int numLecturas,
                     double umbralMin, double umbralMax) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.periodoMs = periodoMs;
        this.numLecturas = numLecturas;
        this.umbralMin = umbralMin;
        this.umbralMax = umbralMax;
    }

    protected abstract double medir();

    @Override
    public void run() {
        for (int i = 1; i <= numLecturas; i++) {
            double valor = medir();
            boolean alerta = valor < umbralMin || valor > umbralMax;
            registrar(valor, alerta);
            Log.log(String.format("%-11s lectura %d/%d: %7.1f %s%s",
                    nombre, i, numLecturas, valor, unidad, alerta ? "  ⚠ ALERTA" : ""));
            try {
                Thread.sleep(periodoMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void registrar(double valor, boolean alerta) {
        lecturas++;
        suma += valor;
        if (valor < minimo) minimo = valor;
        if (valor > maximo) maximo = valor;
        if (alerta) alertas++;
    }

    public String informe() {
        if (lecturas == 0) {
            return String.format("%-11s lecturas=0 (aún sin datos)", nombre);
        }
        return String.format("%-11s lecturas=%d  media=%.1f %s  min=%.1f  max=%.1f  alertas=%d",
                nombre, lecturas, suma / lecturas, unidad, minimo, maximo, alertas);
    }
}