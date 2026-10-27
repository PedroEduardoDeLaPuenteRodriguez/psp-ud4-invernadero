public class Fase2_Concurrente {
    public static void main(String[] args) throws InterruptedException {
        Sensor temperatura = new Temperatura();
        Sensor humedad = new Humedad();
        Sensor luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        Thread hiloTemperatura = new Thread(temperatura, "sensor-Temperatura");
        Thread hiloHumedad = new Thread(humedad, "sensor-Humedad");
        Thread hiloLuz = new Thread(luz, "sensor-Luz");

        long inicio = System.currentTimeMillis();

        hiloTemperatura.start();
        hiloHumedad.start();
        hiloLuz.start();
        riego.start();

        hiloTemperatura.join();
        hiloHumedad.join();
        hiloLuz.join();
        riego.join();

        long total = System.currentTimeMillis() - inicio;

        System.out.println("\n=== INFORME (concurrente) ===");
        System.out.println(temperatura.informe());
        System.out.println(humedad.informe());
        System.out.println(luz.informe());
        System.out.println(riego.informe());
        System.out.println("Tiempo total: " + total + " ms");
    }
}