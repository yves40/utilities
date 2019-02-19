package app;

import org.mouserabbit.utilities.log.Timer;
import org.mouserabbit.utilities.encoders.BASE64Encoder;

public class App {
    public static void main(String[] args) throws Exception {
        Timer tt = new Timer();
        System.out.println("Hello Java");
        Thread.sleep(1000);
        System.out.println("Eleapsed time : " + tt.getTimerString());
        BASE64Encoder b64e = new BASE64Encoder();
        String totot = "ksqlkqoieroz,dskjdsdjkdsjds fjkjqklqel";
        System.out.println(totot);
        System.out.println(b64e.encodeBuffer(totot.getBytes()));
    }
}