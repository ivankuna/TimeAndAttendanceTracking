package hr.betaSoft.tools;

public class OibHandler {

    public static boolean checkOib(String oib) {
        if (oib == null || oib.length() != 11) {
            return false;
        }
        char[] chars = oib.toCharArray();
        int a = 10;
        for (int i = 0; i < 10; i++) {
            char c = chars[i];
            if (c < '0' || c > '9') {
                return false;
            }
            a = a + (c - '0');
            a = a % 10;
            if (a == 0) {
                a = 10;
            }
            a *= 2;
            a = a % 11;
        }
        int control = 11 - a;
        control = control % 10;
        return (control == (chars[10] - '0'));
    }
}