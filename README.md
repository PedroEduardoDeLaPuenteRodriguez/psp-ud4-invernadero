# Invernadero con hilos

Práctica evaluable del Tema 4 · Programación de Servicios y Procesos (2.º DAM).

Autor: Pedro Eduardo De La Puente Rodriguez

---

## Qué hace

Este proyecto simula el control de un invernadero con cuatro dispositivos que trabajan a la vez:

| Dispositivo | Cada | Valores que genera | Rango correcto | Cómo está implementado |
|---|---|---|---|---|
| Sensor de temperatura | 400 ms | 15,0 – 35,0 ºC | 18 – 30 ºC | `Runnable` |
| Sensor de humedad | 600 ms | 40,0 – 90,0 % | 50 – 80 % | `Runnable` |
| Sensor de luz | 500 ms | 0 – 1000 lux | 200 – 900 lux | `Runnable` |
| Programador de riego | 700 ms | abre / cierra la válvula | — | `extends Thread` |

Cada sensor toma 5 lecturas y, si alguna sale del rango correcto, muestra `⚠ ALERTA`. El riego hace 5 ciclos
(abre, cierra, abre, cierra, abre), así que abre 3 veces y riega 7,5 litros en total. Al terminar se imprime un
informe con las estadísticas de cada dispositivo: lecturas, media, mínimo, máximo y alertas.

La práctica se hace en dos versiones para compararlas: una **secuencial** (uno detrás de otro) y otra **concurrente**
(un hilo por dispositivo).

### Estructura del código

| Clase | Qué es |
|---|---|
| `Log` | Imprime la hora, el nombre del hilo que ejecuta el código y el mensaje |
| `Sensor` | Clase abstracta que implementa `Runnable`; contiene las lecturas, las alertas y las estadísticas, y tiene el método abstracto `medir()` |
| `Temperatura`, `Humedad`, `Luz` | Heredan de `Sensor` e implementan `medir()` |
| `ProgramadorRiego` | Hereda de `Thread`; su hilo se llama `riego` |
| `Fase1_Secuencial` | Versión lenta: llama a `run()` uno detrás de otro |
| `Fase2_Concurrente` | Un hilo por dispositivo, y `main` espera con `join()` |
| `Fase3_Experimentos` | Tres experimentos (A, B y C) |
| `Fase4_Vigilante` | Hilo demonio que muestra el estado de los cuatro hilos |

## Cómo ejecutarlo

1. Clonar el repositorio y abrirlo en IntelliJ IDEA con **JDK 21**.
2. Marcar la carpeta `src` como *Sources Root*.
3. Ejecutar el `main` de la clase `Fase…` que se quiera probar (botón ▶ junto al `main`).

Desde la terminal también se puede:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Fase2_Concurrente
```

## Captura

Ejecución de la Fase 2:

<img width="1917" height="1018" alt="Captura de pantalla 2026-10-28 102242" src="https://github.com/user-attachments/assets/40c71dab-b114-4343-b6b2-814ad34e3635" />

Por si la imagen no se ve bien, este es el final de esa ejecución en texto:

```
=== INFORME (concurrente) ===
Temperatura lecturas=5  media=30,2 ºC  min=18,8  max=34,9  alertas=4
Humedad     lecturas=5  media=66,0 %  min=43,8  max=89,0  alertas=2
Luz         lecturas=5  media=328,9 lux  min=76,3  max=905,5  alertas=3
Riego       ciclos=5  litros=7,5 L
Tiempo total: 3592 ms
```

## Resultados

Tiempos medidos:

| Versión | Tiempo medido | Lo que dice la teoría |
|---|---|---|
| Fase 1 (secuencial, llamando a `run()`) | 11165 ms | La suma: 2000 + 3000 + 2500 + 3500 = 11 000 ms |
| Fase 2 (concurrente, `start()` y `join()`) | 3592 ms | El más lento: 3500 ms (el riego) |

Cómo salen los tiempos teóricos: cada dispositivo hace 5 repeticiones y espera su periodo en cada una
(también tras la última). Temperatura: 5 × 400 = 2000 ms. Humedad: 5 × 600 = 3000 ms. Luz: 5 × 500 = 2500 ms.
Riego: 5 × 700 = 3500 ms.

La versión secuencial (Fase 1) tardó **11 165 ms**, muy cerca de los 11 000 ms de la suma teórica (165 ms de más, un 1,5 %).
La concurrente (Fase 2) tardó **3592 ms**, muy cerca de los 3500 ms que corresponden al dispositivo más lento, el riego
(92 ms de más, un 2,6 %). Comparando las dos, la concurrente es unas **3,1 veces más rápida** (11 165 / 3592 ≈ 3,1).
Con esto se cumple la hipótesis de la práctica: secuencial = suma de tiempos y concurrente ≈ el más lento.

También se ve que el informe de la Fase 2 sale completo: 5 lecturas por sensor y 5 ciclos de riego con 7,5 L. Eso significa
que `main` esperó correctamente a los cuatro hilos con `join()` antes de imprimirlo.

> Nota: el diagrama del enunciado habla de 18 s y 6 s, pero con los periodos de la tabla los valores reales son
> 11 s y 3,5 s. La idea (suma frente a máximo) es la misma.

## Análisis

### ¿Por qué compensa usar hilos en el invernadero?

En la versión secuencial el tiempo total es la **suma** de lo que tarda cada dispositivo, porque hasta que no termina
uno no empieza el siguiente. En la concurrente los cuatro trabajan a la vez y el tiempo total es casi el del
dispositivo **más lento**, el riego. Los 92 ms de más que salieron en mi ejecución son el coste de crear y arrancar los
hilos y de que el sistema los vaya turnando.

Además, en la versión secuencial, mientras se espera al sensor de humedad nadie mira la temperatura, y se podría perder
un cambio importante. Con hilos, cada sensor mide a su propio ritmo: en el log de mi ejecución se ve que la temperatura
se lee cada unos 400 ms, la luz cada 500 ms, la humedad cada 600 ms y el riego cambia cada 700 ms, todo a la vez y sin
esperarse entre sí.

Compensa porque los cuatro dispositivos pasan casi todo el tiempo dormidos (`sleep`), y mientras uno espera, otro
puede trabajar. Si fueran cálculos pesados sin esperas, la mejora dependería del número de núcleos del procesador.

**Sobre el orden del log.** El orden no es siempre el mismo. En mi ejecución, aunque arranqué los hilos en el orden
Temperatura, Humedad, Luz y riego, las cuatro primeras líneas salieron en el orden riego, Humedad, Temperatura y Luz, y las
cuatro en el mismo milisegundo. Más adelante, la humedad y la temperatura también coinciden en el mismo instante. Es el
planificador de la JVM quien decide qué hilo va antes, y no puedo controlarlo, por eso el orden puede cambiar de una
ejecución a otra.

### Experimentos A, B y C

**A · informe sin esperar.** Lancé un sensor en un hilo e imprimí su `informe()` sin esperar. La primera vez sale
`lecturas=0`, porque `start()` solo lanza el hilo y `main` sigue enseguida: imprime antes de que el sensor haya medido
nada (`main` no espera a nadie). Después de `hilo.join()` el informe sale completo.

**B · `run()` en vez de `start()`.** Con un riego de 3 ciclos, en el log el hilo que aparece es `main` (no `riego`), tarda
aproximadamente 2100 ms (3 × 700, bloqueando a `main`) y `getState()` devuelve `NEW`. `run()` es un método normal y se
ejecuta en el hilo que lo llama. Solo `start()` pide a la JVM un hilo nuevo. Como nunca se llamó a `start()`, el objeto
no llegó a arrancar como hilo y sigue en `NEW`. Es la misma «PRUEBA» de los cohetes del Ejemplo 0A.

Tiempo medido: 2102 ms (3 ciclos × 700 ms, casi justo lo esperado).

**C · doble `start()`.** Se arranca el riego, se espera a que termine y se llama otra vez a `start()`. Salta
`java.lang.IllegalThreadStateException`. Significa que un hilo solo se puede arrancar una vez: cuando acaba `run()` queda
en `TERMINATED` y no se puede reiniciar. Para repetir el trabajo hay que crear un objeto nuevo.

### Estados observados

| Estado | Cuándo aparece | Qué línea lo provoca |
|---|---|---|
| `NEW` | Antes de arrancar los hilos (y en el experimento B) | `new Thread(...)` o `new ProgramadorRiego()` sin llamar a `start()` |
| `RUNNABLE` | Justo después de arrancar y mientras ejecuta código | `hilo.start()` |
| `TIMED_WAITING` | Casi todo el tiempo de vida de cada dispositivo | `Thread.sleep(periodo)` dentro de `run()` |
| `TERMINATED` | Cuando acaba `run()` (sensores a los 2, 2,5 y 3 s; riego a los 3,5 s) | El fin de `run()` |
| `WAITING` | **No aparece** | Haría falta un `join()` o `wait()` dentro de los cuatro hilos; el único que hace `join()` es `main`, que no está vigilado |
| `BLOCKED` | **No aparece** | Solo ocurre al competir por un cerrojo (`synchronized`); aquí no hay recursos compartidos (UD6) |

**¿Por qué termina el programa si el vigilante sigue en su `while (true)`?** Porque el vigilante es un hilo
**demonio** (`vigilante.setDaemon(true)`, que hay que llamar **antes** de `start()`). La JVM termina cuando solo quedan
hilos demonio y los cierra sin esperarlos. Si no fuera demonio, el programa no terminaría nunca. Si se llama a
`setDaemon(true)` después de `start()`, salta `IllegalThreadStateException`.

### Thread frente a Runnable

| Aspecto | `extends Thread` | `implements Runnable` |
|---|---|---|
| Qué representa | El hilo en sí: la clase *es* un hilo | Una tarea (lo que hay que hacer); necesita un `Thread` que la ejecute |
| Cómo se arranca | Directamente: `riego.start()` | Envolviéndola: `new Thread(sensor, "nombre").start()` |
| Nombre del hilo | Se da en el constructor con `super("riego")` y se lee con `getName()` | No tiene `getName()`: el nombre se pone en el `Thread` y se lee con `Thread.currentThread().getName()` |
| Herencia | Gasta la única herencia que permite Java | Deja libre la herencia (y se pueden implementar más interfaces) |
| Reutilización | Un hilo solo se arranca una vez | La misma tarea se puede pasar a varios `Thread` |
| Diseño | Mezcla «qué hace» con «cómo se ejecuta» | Separa la lógica del mecanismo que la ejecuta |

**¿Por qué los sensores solo pueden ser Runnable en mi diseño?** Porque `Temperatura`, `Humedad` y `Luz` ya heredan
de `Sensor`, donde está la lógica común (lecturas, alertas y estadísticas). Java solo permite heredar de una clase, así
que no pueden heredar también de `Thread`. La única forma de que un hilo los ejecute es que `Sensor` implemente
`Runnable`. Además, así el hilo se crea y se nombra desde fuera (`sensor-Temperatura`, `sensor-Humedad`, `sensor-Luz`),
como se ve en mi captura del log.

**¿Qué forma elegiría para el riego y por qué?** Elegiría `Runnable`. En esta práctica `extends Thread` funciona bien,
porque el riego es una clase suelta, sin jerarquía, que solo necesita un nombre. Pero `Runnable` separa la tarea (abrir y
cerrar la válvula) de quien la ejecuta, deja libre la herencia por si el riego necesitara heredar de otra clase en el futuro
y permite reutilizar la misma tarea o cambiar la forma de ejecutarla sin tocar su código. Con `Thread` me ata a ser un hilo
desde el diseño.

## Conclusión

Lo principal que me llevo de esta práctica es que los hilos compensan cuando hay tareas independientes que pasan mucho
tiempo esperando, como los sensores del invernadero. Los números lo confirman: la versión secuencial suma los tiempos
(11 165 ms) y la concurrente tarda casi lo que el dispositivo más lento (3592 ms frente a 3500 ms teóricos), es decir,
unas 3 veces menos.

También he visto que la concurrencia trae sus propios cuidados. El orden del log cambia entre ejecuciones y no se puede
controlar. Además hay que esperar con `join()` antes de usar los resultados, porque si no `main` imprime el informe vacío
(experimento A). Y hay que distinguir bien entre `start()`, que crea un hilo nuevo, y `run()`, que se ejecuta en el hilo que
lo llama (experimento B). Por último, un hilo solo se arranca una vez (experimento C), y un hilo demonio como el vigilante
permite que el programa termine aunque siga en un bucle infinito.

Los hilos no compensan siempre: si las tareas son muy cortas o dependen unas de otras, el coste de crearlos y coordinarlos
puede ser mayor que lo que se gana.
