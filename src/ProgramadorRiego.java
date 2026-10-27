public class ProgramadorRiego extends Thread {
    private static final long PERIODO_MS = 700;
    private static final double LITROS_POR_APERTURA = 2.5;

    private final int ciclosTotales;
    private volatile int ciclos = 0;
    private volatile double litros = 0;

    public ProgramadorRiego() {
        this(5);
    }

    public ProgramadorRiego(int ciclosTotales) {
        super("riego");
        this.ciclosTotales = ciclosTotales;
    }

    @Override
    public void run() {
        boolean abierta = false;
        for (int i = 1; i <= ciclosTotales; i++) {
            abierta = !abierta;
            if (abierta) {
                litros += LITROS_POR_APERTURA;
            }
            ciclos++;
            Log.log(String.format("Riego ciclo %d/%d: válvula %s  (acumulado: %.1f L)",
                    i, ciclosTotales, abierta ? "ABIERTA" : "cerrada", litros));
            try {
                Thread.sleep(PERIODO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public String informe() {
        return String.format("%-11s ciclos=%d  litros=%.1f L", "Riego", ciclos, litros);
    }
}