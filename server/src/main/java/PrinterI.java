import Demo.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class PrinterI implements Demo.Printer {

    @Override
    public Response printString(String s, com.zeroc.Ice.Current current) {

        // --- INICIO MEDICIÓN DE PERFORMANCE (SERVIDOR) ---
        // Apenas entra la petición, tomamos el tiempo.
        long startTime = System.currentTimeMillis();

        // Desbaratamos el mensaje para sacar el prefijo y el comando.
        // El split con 3 como límite asegura que si el comando tiene ":" no se dañe.
        String[] parts = s.split(":", 3);
        if (parts.length < 3) {
            return new Response(0, "Error: Mensaje malformado. Faltó el prefijo 'user:host:'");
        }
        String clientInfo = parts[0] + "@" + parts[1];
        String message = parts[2];

        System.out.println("\n===> Petición de [" + clientInfo + "]: '" + message + "'");

        String resultValue;

        // Intentamos convertir el mensaje a número. Si se puede, es el punto 2a.
        try {
            int n = Integer.parseInt(message);
            if (n > 0) {
                // Punto 2a: Si el mensaje es un número entero positivo.
                System.out.println("-> Tarea: Fibonacci y Factores Primos para n=" + n);
                printFibonacciSeries(n, clientInfo); // Esto imprime en la consola del server.
                resultValue = "Factores primos de " + n + ": " + getPrimeFactors(n); // Esto se le devuelve al cliente.
            } else {
                resultValue = "Pilas, el número tiene que ser positivo.";
            }
        } catch (NumberFormatException e) {
            // Si no se pudo convertir a número, es un comando de texto.
            if (message.trim().equalsIgnoreCase("listifs")) {
                // Punto 2b: Listar interfaces de red.
                System.out.println("-> Tarea: Listar interfaces de red.");
                resultValue = listNetworkInterfaces();
                System.out.println("--- Interfaces de red para [" + clientInfo + "] ---");
                System.out.println(resultValue);
                System.out.println("-------------------------------------------------");

            } else if (message.trim().startsWith("listports ")) {
                // Punto 2c: Escanear puertos de una IP.
                String ipAddress = message.trim().substring("listports ".length());
                System.out.println("-> Tarea: Escanear puertos en " + ipAddress);
                // Ojo: nmap tiene que estar instalado en el PC del servidor para que esto jale.
                resultValue = executeCommand("nmap " + ipAddress);
                System.out.println("--- Resultado de escaneo para [" + clientInfo + "] ---");
                System.out.println(resultValue);
                System.out.println("-------------------------------------------------");

            } else if (message.trim().startsWith("!")) {
                // Punto 2d: Ejecutar un comando del sistema operativo.
                String command = message.trim().substring(1).trim();
                System.out.println("-> Tarea: Ejecutar comando de SO: '" + command + "'");
                resultValue = executeCommand(command);
                System.out.println("--- Resultado del comando para [" + clientInfo + "] ---");
                System.out.println(resultValue);
                System.out.println("-------------------------------------------------");

            } else {
                // Si no es ninguno de los comandos, pues es un mensaje normal.
                System.out.println("-> Tarea: Devolver eco del mensaje.");
                resultValue = "El servidor recibió tu mensaje: '" + message + "'";
            }
        }

        // --- FIN MEDICIÓN DE PERFORMANCE (SERVIDOR) ---

        // Tomamos el tiempo final para saber cuánto nos demoramos.
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        System.out.println("-> Tarea completada en " + responseTime + " ms.");
        // Devolvemos el objeto Response con el resultado y el tiempo de proceso.
        return new Response(responseTime, resultValue);
    }

    // --- De aquí pa abajo son los métodos que hacen la magia ---

    private void printFibonacciSeries(int n, String clientInfo) {
        System.out.println("--- Imprimiendo Fibonacci para [" + clientInfo + "] (n=" + n + ") ---");
        StringBuilder fibSeries = new StringBuilder();
        int a = 0, b = 1;
        fibSeries.append(a).append(" ");
        while (b <= n) {
            fibSeries.append(b).append(" ");
            int next = a + b;
            a = b;
            b = next;
        }
        System.out.println(fibSeries.toString());
        System.out.println("---------------------------------------------------------");
    }

    private String getPrimeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        int num = n; // Copia para no modificar el original
        for (int i = 2; i * i <= num; i++) {
            while (num % i == 0) {
                factors.add(i);
                num /= i;
            }
        }
        if (num > 1) { // Si queda algo, es un factor primo también
            factors.add(num);
        }
        return factors.toString();
    }

    private String listNetworkInterfaces() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                if (ni.isUp()) { // Solo mostramos las que están activas, porque ajá
                    sb.append("Nombre: ").append(ni.getDisplayName());
                    sb.append(" | MAC: ").append(ni.getHardwareAddress() != null ? bytesToHex(ni.getHardwareAddress()) : "N/A");
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            return "Error sacando las interfaces: " + e.getMessage();
        }
        return sb.toString();
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            // Capturamos tanto la salida normal como los errores
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            while ((line = errorReader.readLine()) != null) {
                output.append("ERROR: ").append(line).append(System.lineSeparator());
            }
            p.waitFor(); // Esperamos a que el comando termine
        } catch (Exception e) {
            return "Pailas ejecutando el comando: " + e.getMessage();
        }
        return output.toString();
    }

    // Un ayudante chiquito para mostrar la MAC address bonita jjajaa
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X-", b));
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "N/A";
    }
}
