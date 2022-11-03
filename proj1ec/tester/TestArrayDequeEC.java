package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomTest() {
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();

        String opString = new String();
        int N = 50000;
        for (int i = 0; i < N; i++) {
            double randomNumber = StdRandom.uniform(0, 4);
            if (randomNumber == 0) {
                Integer randInt = StdRandom.uniform(0,100);
                sad.addFirst(randInt);
                ads.addFirst(randInt);
                opString += "addFirst(" + randInt + ")\n";
            }
            else if (randomNumber == 1) {
                Integer randInt = StdRandom.uniform(0,100);
                sad.addLast(randInt);
                ads.addLast(randInt);
                opString += "addLast(" + randInt + ")\n";
            }
            else if (ads.size() == 0) {
                continue;
            }
            else if (randomNumber == 2) {
                Integer expect = ads.removeFirst();
                Integer actual = sad.removeFirst();
                opString += "removeFirst(): " + actual + "\n";
                assertEquals(opString, expect, actual);
            }
            else if (randomNumber == 3) {
                Integer expect = ads.removeLast();
                Integer actual = sad.removeLast();
                opString += "removeLast(): " + actual + "\n";
                assertEquals(opString, expect, actual);
            }
        }

    }
}
