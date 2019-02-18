package app;

import org.mouserabbit.utilities.log.Timer;;

public class App {
    public static void main(String[] args) throws Exception {
        Timer tt = new Timer();
        System.out.println("Hello Java");
        Thread.sleep(1000);
        System.out.println("Eleapsed time : " + tt.getTimerString());
    }
}