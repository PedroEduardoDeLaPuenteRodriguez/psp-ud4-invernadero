public class Fase1_Secuencial {
    public static void main(String[] args) {
        Sensor temperatura = new Temperatura();
        Sensor humedad = new Humedad();
        Sensor luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        long inicio = System.currentTimeMillis();

        temperatura.run();
        humedad.run();
        luz.run();
        riego.run();

        long total = System.currentTimeMillis() - inicio;

        System.out.println("\n=== INFORME (secuencial) ===");
        System.out.println(temperatura.informe());
        System.out.println(humedad.informe());
        System.out.println(luz.informe());
        System.out.println(riego.informe());
        System.out.println("Tiempo total: " + total + " ms");
    }
}