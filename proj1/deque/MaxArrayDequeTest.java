package deque;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    private static class IntegerComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static class StringSizeComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    private static class StringComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    @Test
    public void TestMax() {
        IntegerComparator c = new IntegerComparator();
        MaxArrayDeque<Integer> array = new MaxArrayDeque<>(c);
        for (int i = 0; i < 100; i ++ ){
            array.addLast(i);
            array.addFirst(i);
        }

        assertEquals((Integer)99, array.max());
    }

    @Test
    public void TestMaxWithoutCompare() {
        IntegerComparator c = new IntegerComparator();
        MaxArrayDeque<Integer> array = new MaxArrayDeque<>(c);
        for (int i = 0; i < 100; i ++ ) {
            array.addLast(i);
            array.addFirst(i);
        }

        assertEquals((Integer)99, array.max(c));
    }

    @Test
    public void TestMaxStringWithSize() {
        StringSizeComparator c = new StringSizeComparator();
        MaxArrayDeque<String> stringDeque = new MaxArrayDeque<>(c);
        String[] s = {"i", "love", "cmh", "so", "much!"};

        for (String string : s) {
            stringDeque.addLast(string);
        }

        assertEquals("much!", stringDeque.max());
    }

    @Test
    public void TestMaxString() {
        StringComparator c = new StringComparator();
        MaxArrayDeque<String> stringDeque = new MaxArrayDeque<>(c);
        String[] s = {"i", "love", "cmh", "very", "much"};

        for (String string: s) {
            stringDeque.addLast(string);
        }

        assertEquals("very", stringDeque.max());
    }
}
