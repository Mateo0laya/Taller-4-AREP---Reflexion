package edu.escuelaing.AREP.Taller1.Services;

import java.util.ArrayList;

import edu.escuelaing.AREP.Taller1.Model.Note;

public class NoteService {

    private ArrayList<Note> notes = new ArrayList<Note>();

    public NoteService() {
        Note myNote = new Note("Great Idea", "I had a great idea, but now, I don't remember it");
        addNote(myNote);
    }

    public void addNote(Note note) {
        notes.add(note);
        System.out.println("Note added!");
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

}
