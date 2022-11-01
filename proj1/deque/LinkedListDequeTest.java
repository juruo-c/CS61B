package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Deque;
import java.util.LinkedList;

/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    /** create a one element Deque and the size of it should be 1 */
    public void nonEmptyInitialTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>(1);

        assertFalse("The Deque should not be empty!", lld.isEmpty());
        assertEquals("The size of Deque should be 1!", 1, lld.size());
    }

    @Test
    /** Test get method */
    public void getTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        for (int i = 0; i < 1000; i ++ )
            lld.addLast(i);
        for (int i = 0; i < 1000; i ++ )
            assertEquals("the " + i + "th element should be " + i, i, (int)lld.get(i));
        assertNull("if the index is out of bound, should return null", lld.get(-1));
        assertNull("if the index is out of bound, should return null", lld.get(1000));
    }

    @Test
    /** Test getRecursive method */
    public void getRecursiveTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        for (int i = 0; i < 1000; i ++ )
            lld.addLast(i);
        for (int i = 0; i < 1000; i ++ )
            assertEquals("the " + i + "th element should be " + i, i, (int)lld.getRecursive(i));
        assertNull("if the index is out of bound, should return null", lld.getRecursive(-1));
        assertNull("if the index is out of bound, should return null", lld.getRecursive(1000));
    }

    @Test
    /** random test all methods */
    public void randomTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        Deque<Integer> de = new LinkedList<>();

        int N = 100000;
        for (int i = 0; i < N; i ++ ) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 1) {
                int randInt = StdRandom.uniform(0, 100);
                lld.addFirst(randInt);
                de.addFirst(randInt);
            }
            else if (operationNumber == 2) {
                int randInt = StdRandom.uniform(0, 100);
                lld.addLast(randInt);
                de.addLast(randInt);
            }
            else if (lld.size() == 0) {
                assertEquals(lld.size(), de.size());
                assertEquals(lld.isEmpty(), de.isEmpty());
            }
            else if (operationNumber == 3) {
                assertEquals(lld.removeFirst(), de.removeFirst());
            }
            else if (operationNumber == 4) {
                assertEquals(lld.removeLast(), de.removeLast());
            }
            else if (operationNumber == 5) {
                assertEquals(lld.get(0), de.getFirst());
                assertEquals(lld.get(lld.size() - 1), de.getLast());
                assertEquals(lld.getRecursive(lld.size() - 1), de.getLast());
                assertEquals(lld.getRecursive(0), de.getFirst());
            }
        }
    }

    @Test
    public void testEqual() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld3 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i ++ ) {
            lld1.addLast(i);
            lld2.addLast(i);
            lld3.addLast(100 - i);
        }

        assertTrue(lld1.equals(lld1));
        assertFalse(lld1.equals(null));
        int cmh = 21;
        assertFalse(lld1.equals(cmh));
        assertTrue(lld1.equals(lld2));
        assertFalse(lld1.equals(lld3));

        LinkedListDeque<String> lld4 = new LinkedListDeque<>();
        LinkedListDeque<String> lld5 = new LinkedListDeque<>();
        LinkedListDeque<String> lld6 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i ++ ) {
            lld1.addLast(i);
            lld2.addLast(i);
            lld3.addLast(100 - i);
        }

        assertTrue(lld1.equals(lld2));
        assertFalse(lld1.equals(lld3));

    }

    @Test
    public void testIterator() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        for (int i = 0; i < 100; i ++ )
            lld.addLast(i);
        int i = 0;
        for (int x : lld){
            assertEquals(i, x);
            i += 1;
        }
    }
}
