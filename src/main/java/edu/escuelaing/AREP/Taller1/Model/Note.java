package edu.escuelaing.AREP.Taller1.Model;

public class Note {

    private String title;
    private String description;

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\": \"" + title + "\"," +
                "\"description\": \"" + description + "\"," +
                "}";
    }
}
