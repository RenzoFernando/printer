import java.io.*;

public class Server
{
    // Este archivo es el que arranca todo en el lado del servidor.
    // Su única misión es iniciar el comunicador de Ice, crear el objeto que atiende
    // y quedarse esperando a que lleguen los clientes.

    // Este se runea primero y luego el cliente.java
    public static void main(String[] args)
    {
        // lista para guardar argumentos extra que no reconoce Ice.
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        // pa asegurar que el 'communicator' se cierre solo al final segun entendimos.
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.server",extraArgs))
        {
            // revisa si le pasamos argumentos de más al programa al ejecutarlo.
            // No es súper clave para la tarea, pero es una buena práctica segun se nos explico.
            if(!extraArgs.isEmpty())
            {
                System.err.println("Ojo, le pasaste argumentos de más.");
                for(String v:extraArgs){
                    System.out.println(v);
                }
            }

            // Se creo un "adaptador", que es como un puerto de escucha para nuestras peticiones.
            // El nombre "Printer" debe coincidir con el que pusimos en config.server
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Printer");

            // Creamos una instancia de nuestra lógica, la clase PrinterI.
            // Este es el objeto que de verdad va a hacer el trabajo.
            com.zeroc.Ice.Object object = new PrinterI();

            // Le decimos al adaptador que cuando pregunten por "SimplePrinter",
            // debe usar el objeto que acabamos de crear.
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));

            // ¡Activamos el adaptador y nos ponemos a escuchar!
            adapter.activate();
            System.out.println("Servidor iniciado y esperando clientes...");

            // Esto hace que el servidor no se apague y se quede corriendo hasta que lo cerremos (Ctrl+C).
            communicator.waitForShutdown();
        }
    }

    // El método f(String m) no se utilizo.
    // Es un método para ejecutar comandos en el SO, pero esa lógica ya la pusimos
    // dentro de PrinterI.java (en el método executeCommand), que es donde debe estar segun nosotros investigamos.
    public static void f(String m)
    {
        String str = null, output = "";

        InputStream s;
        BufferedReader r;

        try {
            Process p = Runtime.getRuntime().exec(m);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream())); 
            while ((str = br.readLine()) != null) 
            output += str + System.getProperty("line.separator"); 
            br.close(); 
        }
        catch(Exception ex) {
        }
    }

}