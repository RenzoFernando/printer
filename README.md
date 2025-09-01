# IceShell: Consola Remota con Java y ZeroC Ice

## Descripción del Proyecto

Este es un proyecto académico desarrollado para la asignatura de Sistemas Distribuidos. El objetivo principal es transformar una aplicación cliente-servidor básica, basada en el middleware ZeroC Ice, en una shell de comandos remota con funcionalidades extendidas.

La aplicación permite a un cliente enviar diversos tipos de comandos a un servidor, el cual los procesa y retorna los resultados correspondientes a través de la red.

## Funcionalidades

El cliente opera como una consola interactiva que acepta las siguientes instrucciones:

* **Número entero positivo**: Al recibir un número, el servidor imprime la serie de Fibonacci correspondiente en su consola local y retorna al cliente un listado con los factores primos de dicho número.
    * Ejemplo: `100`

* **`listifs`**: Retorna una lista de las interfaces de red activas en la máquina del servidor.

* **`listports [IPv4]`**: El servidor ejecuta una tarea de escaneo de puertos sobre la dirección IPv4 especificada (requiere `nmap`) y retorna el resultado al cliente.
    * Ejemplo: `listports 127.0.0.1`

* **`![comando]`**: Ejecuta el comando proporcionado en el sistema operativo del servidor y retorna la salida estándar (stdout/stderr) al cliente.
    * Ejemplo (Windows): `!ipconfig`
    * Ejemplo (Linux/macOS): `!ifconfig`

## Tecnologías Implementadas

* **Lenguaje de Programación**: Java (JDK 11)
* **Middleware**: ZeroC Ice 3.7
* **Sistema de Construcción y Dependencias**: Gradle 6.6 (mediante Gradle Wrapper)

## Instrucciones de Ejecución

### Requisitos Previos

1.  Java Development Kit (JDK) versión 11.
2.  `nmap` instalado en la máquina que ejecutará el servidor (necesario para la funcionalidad `listports`).

### 1. Compilación

Navegue a la raíz del proyecto desde una terminal y ejecute el siguiente comando para compilar las aplicaciones de cliente y servidor:

```bash
# Para Windows
.\gradlew.bat clean build

# Para Linux o macOS
./gradlew clean build
````

### 2\. Ejecución del Servidor

En una terminal, ejecute el archivo JAR generado para el servidor:

```bash
java -jar server/build/libs/server.jar
```

**Nota:** Hay que verifique que el `host` y `puerto` en el archivo `server/src/main/resources/config.server` sean los correctos para su entorno de red.

### 3\. Ejecución del Cliente

En una nueva terminal, ejecute el archivo JAR del cliente:

```bash
java -jar client/build/libs/client.jar
```

**Nota:** Hay que asegúrese de que el archivo `client/src/main/resources/config.client` apunte a la dirección IP y puerto donde se está ejecutando el servidor.

## Autores

  * Luna CatalinaMartínez A00401964
  * Renzo Fernando Mosquera A00401681

