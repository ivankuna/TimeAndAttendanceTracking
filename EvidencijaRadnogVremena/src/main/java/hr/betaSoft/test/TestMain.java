package hr.betaSoft.test;

public class TestMain {

    public static void main(String[] args) {

        String time = "15:30";

        char[] charArray = time.toCharArray();

        String hours = String.valueOf(charArray[0]) + charArray[1];

        int min = 135 % 60;

        System.out.println(min);
    }
}
