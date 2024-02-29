package edu.escuelaing.AREP.Taller1.Controller;

import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import edu.escuelaing.AREP.Taller1.Annotation.Component;
import edu.escuelaing.AREP.Taller1.Annotation.RequestMapping;
import edu.escuelaing.AREP.Taller1.Services.Function;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServer {

    private static HashMap<String, String> cache = new HashMap<String, String>();
    private static HttpConnection httpConnection = new HttpConnection();

    private static String serviceUri = "";
    private static Function service = null;
    private static boolean running = false;

    private static HttpServer _instance = new HttpServer();

    private static HashMap<String, Function> getFunctions = new HashMap<String, Function>();
    private static HashMap<String, Function> postFunctions = new HashMap<String, Function>();

    private static Map<String, Method> components = new HashMap<String, Method>();

    private static String resourcePath = "target/classes/public";

    public HttpServer() {
    }

    public static HttpServer getInstance() {
        return _instance;
    }

    public static void runServer(String[] args) throws IOException, URISyntaxException {

        String directory = "target\\classes\\edu\\escuelaing\\AREP\\Taller1";
        loadClasses(directory);

        ServerSocket serverSocket = null;

        int port = 35000;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
        }

        Socket clientSocket = null;

        running = true;

        while (running) {

            try {
                System.out.println("Ready to recive ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String uriStr = "";
            String method = "";
            String requestBody = "";

            int length = 0;

            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {
                if (firstLine) {
                    uriStr = inputLine.split(" ")[1];
                    method = inputLine.split(" ")[0];
                    firstLine = false;
                }
                if (inputLine.startsWith("Content-Length")) {
                    length = Integer.parseInt(inputLine.split(" ")[1]);
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            if (length > 0) {
                char[] bodyBuffer = new char[length];
                in.read(bodyBuffer, 0, length);
                requestBody = new String(bodyBuffer);
            }

            URI requestUri = new URI(uriStr);

            try {
                if (requestUri.getPath().startsWith("/calculator")) {
                    outputLine = callService(requestUri, method, requestBody);
                } else if (requestUri.getPath().startsWith("/components")) {
                    outputLine = responseComponent(requestUri);
                } else {
                    outputLine = httpResponse(uriStr, clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
                outputLine = httpError();
            }

            out.println(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    private static String responseComponent(URI requestUri) throws IllegalAccessException, InvocationTargetException {
        String output = "";
        String path = requestUri.getPath().substring(11);
        String query = requestUri.getQuery().split("=")[1];
        System.out.println(path);

        Method m = components.get(path);

        if (components.containsKey(path)) {
            if (query != null) {
                output = m.invoke(null, query).toString();
            } else {
                output = m.invoke(null).toString();
            }
        }
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + output;
    }

    private static void loadClasses(String directory) throws IOException {
        Files.walk(Path.of(directory))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".class"))
                .forEach(p -> {
                    try {
                        String className = p.toString().split("classes\\\\")[1].replace("\\", ".").split(".class")[0];
                        Class c = Class.forName(className);
                        if (c.isAnnotationPresent(Component.class)) {
                            for (Method m : c.getDeclaredMethods()) {
                                if (m.isAnnotationPresent(RequestMapping.class)) {
                                    components.put(m.getAnnotation(RequestMapping.class).value(), m);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static String callService(URI requestUri, String method, String requestBody) {
        String calledServiceUri = requestUri.getPath().substring(11);
        String output = "";

        if (method.equals("GET") && getFunctions.containsKey(calledServiceUri)) {
            service = getFunctions.get(calledServiceUri);
            output = service.handle(requestUri.getQuery(), output);
        } else if (method.equals("POST") && postFunctions.containsKey(calledServiceUri)) {
            service = postFunctions.get(calledServiceUri);
            output = service.handle(requestBody, output);
        }
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + output;
    }

    public static void get(String path, Function svc) throws IOException, URISyntaxException {
        if (!getFunctions.containsKey(path)) {
            getFunctions.put(path, svc);
        }
    }

    public static void post(String path, Function svc) throws IOException, URISyntaxException {
        if (!postFunctions.containsKey(path)) {
            postFunctions.put(path, svc);
        }
    }

    private static String httpResponse(String uriStr, Socket clientSocket) throws IOException {
        String outputLine;
        if (uriStr.startsWith("/hello")) {
            outputLine = searchMovie(uriStr);
        } else {
            String fileType = uriStr.split("\\.")[1];
            outputLine = getHeader(fileType);

            if (fileType.equals("jpg")) {
                sendImage(uriStr, clientSocket, outputLine);
                outputLine = "";
            } else {
                outputLine = outputLine + sendText(uriStr);
            }

        }
        return outputLine;
    }

    private static String sendText(String uriStr) throws IOException {
        String outputLine = "";
        Path file = Paths.get(resourcePath + uriStr);

        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader = Files.newBufferedReader(file, charset);
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            outputLine = outputLine + line;
        }
        return outputLine;
    }

    private static void sendImage(String uriStr, Socket clientSocket, String header) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        out.write(header.getBytes());
        File file = new File(resourcePath + uriStr);
        Files.copy(file.toPath(), out);
        out.close();
    }

    public static String getHeader(String fileType) {
        fileType = (fileType.equals("jpg")) ? "image/" + fileType : "text/" + fileType;
        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type:" + fileType + "\r\n"
                + "\r\n";
        return header;
    }

    public static String searchMovie(String uriString) {
        String moviesName = uriString.split("=")[1];
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n";
        if (cache.containsKey(moviesName)) {
            outputLine = outputLine + cache.get(moviesName);
            System.out.println("Del caché");
        } else {
            try {
                outputLine = outputLine + httpConnection.query(moviesName);
                cache.put(moviesName, outputLine);
            } catch (IOException e) {
                outputLine = httpError();
                System.out.println("Error: " + e.getMessage());
            }
        }
        return outputLine;
    }

    public static String httpError() {
        String outputLine = "HTTP/1.1 400 Not Found\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Error Not Found</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Error</h1>\n" +
                "    </body>\n";
        return outputLine;
    }

    public static ArrayList<String> getParameters(String URI) {
        String[] rawSplit = URI.split("&");
        String part1 = rawSplit[0];
        String part2 = rawSplit[1];

        String x = part1.split("=")[1];
        String y = part2.split("=")[1];

        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(x);
        parameters.add(y);

        return parameters;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}