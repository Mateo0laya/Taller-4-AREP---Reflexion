package edu.escuelaing.AREP.Taller1.Controller;

import edu.escuelaing.AREP.Taller1.Annotation.Component;
import edu.escuelaing.AREP.Taller1.Annotation.RequestMapping;

@Component
public class Hellocontroller {

    @RequestMapping("/hello")
    public static String hello() {
        return "Hello";
    }

    @RequestMapping("/bye")
    public static String bye() {
        return "Bye";
    }

    @RequestMapping("/name")
    public static String name(String name) {
        return "Your name is " + name + "?";
    }
}
