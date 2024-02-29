# TALLER 3: MICROFRAMEWORK WEB

Este proyecto es una continuación del proyecto anterior el cual se puede encontrar en el siguiente repositorio:
https://github.com/Mateo0laya/TALLER-2---DISENO-Y-ESTRUCTURACION-DE-APLICACIONES-DISTRIBUIDAS-EN-INTERNET
Para esta ocasión el objetivo es realizar una pequeña implementación de un framework web tipo spark, el cual soporte las operaciones de GET y POST haciendo uso de las funciones lambda, como se define a continuación

## Arquitectura 
La arquitectura debe tener las siguientes características.

1. El cliente Web debe ser un cliente asíncrono que corra en el browser  y use Json como formato para los mensajes.
2. El servidor de servirá como un gateway para encapsular llamadas a otros servicios Web externos.
3. La aplicación debe ser multiusuario.
4. Todos los protocolos de comunicación serán sobre HTTP.
5. Los formatos de los mensajes de intercambio serán siempre JSON.
6. La interfaz gráfica del cliente debe ser los más limpia y agradableolo HTML y JS (Evite usar librerías complejas). Para invocar métodos REST desde el cliente usted puede utilizar la tecnología que desee.
7. Debe construir un cliente Java que permita probar las funciones del servidor fachada. El cliente utiliza simples conexiones http para conectarse a los servicios. Este cliente debe hacer pruebas de concurrencia en su servidor de backend.
8. La fachada de servicios tendrá un caché que permitirá que llamados que ya se han realizado a las implementaciones concretas con parámetros específicos no se realicen nuevamente. Puede almacenar el llamado como un String con su respectiva respuesta, y comparar el string respectivo. Recuerde que el caché es una simple estructura de datos.
9. Se debe poder extender fácilmente, por ejemplo, es fácil agregar nuevas funcionalidades, o es fácil cambiar el proveedor de una funcionalidad.
10. Debe utilizar maven para gestionar el ciclo de vida, git y github para almacenar al código fuente y heroku como plataforma de producción.
11. En el backend debe utilizar solo Java. No puede utilizar frameworks como SPRING.
12. Permite configurar servicios web de tipo GET y POST usando funciones lambda.
13. Entrega archivos estáticos como páginas HTML, CSS, JS e imágenes.
14. Permite configurar el directorio de donde se leerán los archivos estáticos.
15. Permite leer parámetros del query  desde los programas.

## Diseño de la aplicación

La aplicacipon fue diseñada de tal manera que se cumplan los requisitos establecidos en la arquitectura.

- La clase `HttpServer` contiene la lógica del servidor. Recibe las solicitudes de los usuarios y hace el llamado a la API.
- El servidor entrega a los usuarios un cliente asíncrono al cual pueden acceder desde cualquier navegador.
- Al realizar una consulta desde la aplicación, el servidor hace el llamado a una API externa.
- La respuesta que es dada por la API es validada para devolver al usuario la información correcta. En caso de que la película no se encuentra en la API, se mostrará al usuario un mensaje adecuado.
- La clase `HttpConnection` realiza la conexión a OMDb API en el método `query`, al cual se le pasa como argumento el título de la película. Si la película es encontrada, se retorna un String con los datos, de lo contrario, se establecen mecanismos para validar si la película no fue encontrada y mostrar al usuario el estado de la consulta.
- `HttpServer` almacena en una estructura de datos concurrente `HashMap` un caché de las consultas hechas a la API, lo que acorta considerablemente los tiempos de respuesta.
- La clase 'HttpServer' ya no contiene los archivos web como HTML, CSS y JavaScript
- Los archivos web se encuentran en un nuevo directorio
- La clase MyServices define el comportamiento del servidor a las peticiones GET y POST a través de funciones lambda facilmente modificables.
- La clase Note define un tipo de objeto con la finalidad de testear la funcionalidad del método POST.
- La clase NoteService conecta la logica de la clase Note con la vista de la aplicación
- La interfaz Function define el método handle.
- ideas.html es la pagina web donde se testea el método POST.

# Extensión de la aplicación

- Desde la clase HttpConnection se encuentran guardadas las variables como url para ocnsultar a la API, desde alli basta con crear nuevos metodos modificando la url para agregar nuevas funcionalidades que permite la API.
- En la clase HttpServer se agrego una variable port, la cual permite cambiar de manera rapida y sencilla el puerto atraves del cual se ejecutará el servidor, ya sea por que el 35000 se encuentra ocupado o por que desean establecer la comunicación po run puerto especifico, se recomienda no hacer uso de los well known ports
- De momento unicamente soporta archivos multimedia con extensión .jpg por lo que a un futuro deberá aceptar otro tipo de archivos
- Por parte de calculator existe una infinidad de posibilidades a la hora de implementar una calculadora usando el método GET, se puede implementar cualquier operación matemática.
- Crear una visualización adecuada y llamativa para las funcionalidades del framework
- Existe la opción de cambiar la ruta donde se encuentran los recursos estaticos para lo cual debemos descomentar y editar la linea 19 de Myservices.java

## Guia de inicio

Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba. Consulte implementación para obtener notas sobre cómo implementar el proyecto en un sistema en vivo.

### Prerequisitos

- Java 8
- Maven
- Git
- Navegador web

### Instalación

Ubiquese en el directorio en donde desea descargar el repositorio

`git clone https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB.git`

Cambie al directorio del repositorio

`cd Taller-3-Microframeworks-WEB.git`

Compile el proyecto

`mvn compile`

Empaquete el proyecto

`mvn package`

Inicie el servidor

`java -cp target\appps-distribuidas-1.0-SNAPSHOT.jar edu.escuelaing.AREP.Taller1.HttpServer`

Una alternativa a la linea de comandos es realizar la ejecución desde un IDE. En este caso Visual Studio Code desde la clase MyServices.java
![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/861bb52c-adf7-4374-a748-344d51b30b8d)

## Probando la aplicación

Una vez recibimos el mensaje de confirmacion (Ready to recive...) estamos listos para acceder a los recursos estaticos del servidor desde un navegador como Google chrome, Mozilla, Safari, etc.
Para lo cual debemos dirigirmos a la dirección
http://localhost:35000/index.html

En caso de cambiar el puerto debemos reemplazar 35000 por el puerto que seleccionamos en el codigo fuente de la aplicación

Una vez dentro encontraremos una interfaz muy sencilla invitandonos a introducir el nomobre de la pelicula
![image](https://github.com/Mateo0laya/TALLER-2---DISENO-Y-ESTRUCTURACION-DE-APLICACIONES-DISTRIBUIDAS-EN-INTERNET/assets/89365336/b685782a-4c9c-48bb-af9c-4004a269960d)

Es importante que al introducir el titulo de la pelicula que deseamos buscar, realicemos la busqueda dando click en el boton "submit", de lo contrario al dar enter no mostrará el resultado correctamente

A partir de aqui plantemaos dos posibles escenarios

### Escenario #1: El nombre de la pelicula fue encontrado en la API
En este caso recibiremos la información correspondiente a la pelicula que deseamos consultar.
![image](https://github.com/Mateo0laya/Taller-1-Aplicacion-distribuidas---AREP/assets/89365336/858bea99-3c08-4db4-a9a4-8600b1682ea2)

### Escenario #2: El nombre de la pelicula no fue encontrado en la API
En este caso recibiremos un error indicandonos que la pelicula no fue encontrada
![image](https://github.com/Mateo0laya/Taller-1-Aplicacion-distribuidas---AREP/assets/89365336/114138e5-0588-4071-8319-d13a36a7e1f9)

### Windows
#### GET
Una vez recibimos el mensaje de confirmacion (Ready to recive...) estamos listos para acceder al servidor desde un navegador como Google chrome, Mozilla, Safari, etc.
Para lo cual debemos dirigirmos a la dirección
http://localhost:35000/calculator/hello?Mateo

En caso de cambiar el puerto debemos reemplazar 35000 por el puerto que seleccionamos en el codigo fuente de la aplicación

Una vez dentro veremos un amistoso saludo
![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/e2b1ac04-9fd5-4a11-99e4-4b9817ac17f6)


Tenemos una variedad de funcionalidades para probar:
- Square:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/764d202f-8242-466a-830d-cf1ff00a5c76)
- Square root:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/9652aa77-339e-4e85-b292-8a44073e2d2e)
- Cos:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/9f9c73de-1fc3-43f8-acdc-f76a0a9766c2)
- Sin:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/91d1f3f9-19b8-4f4f-9b48-e51c567e3559)
- Addition:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/2324bd88-3b52-4a53-a5e9-452522b5bd86)
- Ideas:

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/f8b54ae0-3de7-4feb-8d14-9f38cd9cb658)

#### POST
Para el POST debemos dirirgirnos a la dirección:
http://localhost:35000/ideas.html

![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/1f38682c-4144-4f9f-8688-c6eef15d643e)


Allí encontraremos un formulario para añadir nuestras ideas, se enviará y procesara en el servidor y nos dara una respuesta con las ideas en formato JSON
![image](https://github.com/Mateo0laya/Taller-3-Microframeworks-WEB/assets/89365336/cf56095f-363e-4ab8-a68b-7c5c21b7560a)


### Linux

Usando una maquina virtual de Ubuntu podemos ver que igualmente corre la aplicación


## Pruebas unitarias

Para correr las pruebas unitarias del proyecto, desde la terminal y en el directorio del proyecto ejecutamos
'mvn test' 

![image](https://github.com/Mateo0laya/TALLER-2---DISENO-Y-ESTRUCTURACION-DE-APLICACIONES-DISTRIBUIDAS-EN-INTERNET/assets/89365336/30261f3c-e080-44bf-8ce4-52555bfe87f6)
De igual modo lo podremos realizar desde el entorno grafico del IDE de nuestra preferencia

## Construido con

* [Java](https://www.java.com/es/) - The main programming language
* [Maven](https://maven.apache.org/) - Dependency Management

## Version

Version 1.0.0.

## Autor

Mateo Olaya Garzon
