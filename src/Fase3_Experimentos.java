public class Fase3_Experimentos {
    public static void main(String[] args) throws InterruptedException {
        experimentoA();
        experimentoB();
        experimentoC();
    }

    private static void experimentoA() throws InterruptedException {
        System.out.println("\n##### EXPERIMENTO A: informe sin join #####");
        Sensor luz = new Luz();
        Thread hilo = new Thread(luz, "sensor-Luz");
        hilo.start();
        System.out.println("Sin esperar -> " + luz.informe());
        hilo.join();
        System.out.println("Tras join   -> " + luz.informe());
    }

    private static void experimentoB() {
        System.out.println("\n##### EXPERIMENTO B: run() en vez de start() #####");
        ProgramadorRiego riego = new ProgramadorRiego(3);
        long inicio = System.currentTimeMillis();
        riego.run();
        long total = System.currentTimeMillis() - inicio;
        System.out.println("Tiempo: " + total + " ms");
        System.out.println("riego.getState() = " + riego.getState());
    }

    private static void experimentoC() throws InterruptedException {
        System.out.println("\n##### EXPERIMENTO C: doble start() #####");
        ProgramadorRiego riego = new ProgramadorRiego(2);
        riego.start();
        riego.join();
        System.out.println("Estado tras terminar: " + riego.getState());
        try {
            riego.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("Excepción capturada: " + e);
        }
    }
}