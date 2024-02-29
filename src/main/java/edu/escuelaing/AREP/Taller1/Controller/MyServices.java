package edu.escuelaing.AREP.Taller1.Controller;

import static edu.escuelaing.AREP.Taller1.Controller.HttpServer.get;
import static edu.escuelaing.AREP.Taller1.Controller.HttpServer.post;
import static edu.escuelaing.AREP.Taller1.Controller.HttpServer.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import edu.escuelaing.AREP.Taller1.Model.Note;
import edu.escuelaing.AREP.Taller1.Services.NoteService;

public class MyServices {
    public static void main(String[] args) throws IOException, URISyntaxException {

        NoteService noteService = new NoteService();

        // HttpServer.setResourcePath("path")

        get("/hello", (req, res) -> {
            return "Hello! How are you  " + req + "?";
        });

        get("/square", (req, res) -> {
            double base = Double.parseDouble(req);
            double square = base * base;
            String answer = String.valueOf(square);
            return "The square of " + req + " is: " + answer;
        });

        get("/sqrt", (req, res) -> {
            double base = Double.parseDouble(req);
            double sqrt = Math.sqrt(base);
            String answer = String.valueOf(sqrt);
            return "The square root of " + req + " is: " + answer;
        });

        get("/cos", (req, res) -> {
            double rad = Double.parseDouble(req);
            double cos = Math.cos(rad);
            String answer = String.valueOf(cos);
            return "The cos of " + req + " is: " + answer;
        });

        get("/sin", (req, res) -> {
            double rad = Double.parseDouble(req);
            double sin = Math.sin(rad);
            String answer = String.valueOf(sin);
            return "The sin of " + req + " is: " + answer;
        });

        get("/addition", (req, res) -> {
            ArrayList<String> parameters = HttpServer.getParameters(req);
            double x = Double.parseDouble(parameters.get(0));
            double y = Double.parseDouble(parameters.get(1));

            double total = x + y;
            String answer = String.valueOf(total);
            String xStr = String.valueOf(x);
            String yStr = String.valueOf(y);

            return xStr + " + " + yStr + " = " + answer;
        });

        get("/ideas", (req, res) -> {
            return noteService.getNotes().toString();
        });

        post("/ideas", (req, res) -> {
            ArrayList<String> parameters = HttpServer.getParameters(req);
            String title = parameters.get(0);
            String description = parameters.get(1);
            Note newNote = new Note(title, description);
            noteService.addNote(newNote);
            return noteService.getNotes().toString();
        });

        HttpServer.getInstance().runServer(args);
    }
}
