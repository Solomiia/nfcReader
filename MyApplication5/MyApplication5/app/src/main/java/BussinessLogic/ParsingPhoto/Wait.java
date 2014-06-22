package BussinessLogic.ParsingPhoto;

/**
 * Created by Solomiia on 4/13/14.
 */
public class Wait {
    public static void oneSec() {
        try {
            Thread.currentThread().sleep(10);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void manySec(long s) {
        try {
            Thread.currentThread().sleep(s * 10);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

