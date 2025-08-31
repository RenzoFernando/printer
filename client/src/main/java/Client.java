import java.util.Scanner;
import java.net.InetAddress;
import Demo.Response;

// Cliente interactivo que se conecta al servidor usando ICE.
// No ejecutar antes de iniciar el servidor.
public class Client {
    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {

            // para conectarnos al proxy del servidor, usando la info de config.client
            Demo.PrinterPrx service = Demo.PrinterPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy"));

            // Si el servicio es nulo, pailas, no nos pudimos conectar.
            if (service == null) {
                throw new Error("Proxy inválido. ¿Será que el servidor no está corriendo?");
            }

            Scanner scanner = new Scanner(System.in);
            String userInput;

            // Punto 1: Sacamos el username y el hostname para el prefijo.
            String username = System.getProperty("user.name");
            String hostname = InetAddress.getLocalHost().getHostName();

            System.out.println("--- Cliente Interactivo Conectado ---");
            System.out.println("Escribe un comando o mensaje. Para salir, escribe 'exit'.");

            // Punto 3: Un ciclo infinito hasta que el man escriba "exit".
            while (true) {
                System.out.print(username + "@" + hostname + "> ");
                userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break; // Nos salimos del while.
                }

                // Punto 1: Armamos el mensaje completo con el prefijo "user:host:mensaje"
                String messageToSend = username + ":" + hostname + ":" + userInput;

                // --- INICIO MEDICIÓN DE PERFORMANCE (CLIENTE) ---
                // Aquí tomamos el tiempo justo antes de enviar la petición.
                long clientStartTime = System.currentTimeMillis();

                // Hacemos la llamada al servidor. El programa se queda aquí esperando la respuesta.
                Response response = service.printString(messageToSend);

                // Y aquí tomamos el tiempo justo cuando llega la respuesta.
                long clientEndTime = System.currentTimeMillis();
                long roundTripTime = clientEndTime - clientStartTime; // El tiempo total de ida y vuelta.
                // --- FIN MEDICIÓN DE PERFORMANCE (CLIENTE) ---


                // Imprimimos la respuesta que nos llegó del servidor.
                System.out.println("Respuesta del Servidor: " + response.value);

                // Imprimimos nuestras mediciones de performance.
                System.out.println("  -> Tiempo de procesamiento del Servidor: " + response.responseTime + " ms");
                System.out.println("  -> Tiempo total (Round-Trip Time): " + roundTripTime + " ms");
                System.out.println("------------------------------------");
            }

            System.out.println("Cerrando el cliente. ¡Nos vemos, bye bye!");
            scanner.close();

        } catch (Exception e) {
            System.err.println("Ocurrió un error grave en el cliente.");
            e.printStackTrace();
        }
    }
}
