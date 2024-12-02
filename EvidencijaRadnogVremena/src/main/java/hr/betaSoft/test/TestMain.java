package hr.betaSoft.test;

import java.util.*;

public class TestMain {

    public static void main(String[] args) {

        String test1 = "oVo JE TEST";

        String[] test2 = test1.split(" ");

        List<String> test3 = Arrays.asList(test2);

        for (String str : test3) {
            test3.set(test3.indexOf(str), str.toLowerCase());
        }

        System.out.println("stop");
    }
}
