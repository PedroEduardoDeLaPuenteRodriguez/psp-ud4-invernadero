public class Fase4_Vigilante {
    public static void main(String[] args) throws InterruptedException {
        Sensor temperatura = new Temperatura();
        Sensor humedad = new Humedad();
        Sensor luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        Thread hiloTemperatura = new Thread(temperatura, "sensor-Temperatura");
        Thread hiloHumedad = new Thread(humedad, "sensor-Humedad");
        Thread hiloLuz = new Thread(luz, "sensor-Luz");
        Thread[] hilos = {hiloTemperatura, hiloHumedad, hiloLuz, riego};

        Thread vigilante = new Thread(() -> {
            while (true) {
                mostrarEstados("vigilante", hilos);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "vigilante");
        vigilante.setDaemon(true);   // antes de start()

        mostrarEstados("ANTES de arrancar", hilos);

        hiloTemperatura.start();
        hiloHumedad.start();
        hiloLuz.start();
        riego.start();
        vigilante.start();

        for (Thread h : hilos) {
            h.join();
        }

        mostrarEstados("DESPUÉS de esperar", hilos);
        System.out.println("main: he terminado");
    }

    private static void mostrarEstados(String etiqueta, Thread[] hilos) {
        StringBuilder sb = new StringBuilder("ESTADOS (" + etiqueta + "): ");
        for (Thread h : hilos) {
            sb.append(h.getName()).append("=").append(h.getState()).append("  ");
        }
        Log.log(sb.toString());
    }
}