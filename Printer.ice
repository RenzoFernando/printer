module Demo
{
    class Response{
        // El tiempo que se demoró el server procesando, en milisegundos
        long responseTime;
        // La respuesta como tal, lo que sea que el server nos mande de vuelta
        string value;
    };
    interface Printer
    {
        Response printString(string s);
    }
}