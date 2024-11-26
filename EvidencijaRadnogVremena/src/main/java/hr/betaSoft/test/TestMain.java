package hr.betaSoft.test;

import java.sql.Date;
import java.util.*;

public class TestMain {

    public static void main(String[] args) {

        MojTest test = new MojTest();

        if (!Objects.equals(test.getTest(), null) && test.getTest().trim().isEmpty()) {

            System.out.println("test");

        }
    }
}
