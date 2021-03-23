package uk.ac.shef.com3529.lectures;

public class SignUtils {

    public static boolean isNegative(int n) {
        return n < 0;
    }

    public static boolean isPositive(int n) {
        return n > 0;
    }

    public static int sign(int n) {
        int s = 0;

        if (n > 0) {
            s = 1;
        }

        if (n < 0) {
            s = -1;
        }

        return s;
    }
}
