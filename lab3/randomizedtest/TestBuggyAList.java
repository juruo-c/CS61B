package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testTreeAddTreeRemove(){
        AListNoResizing<Integer> NomalList = new AListNoResizing<>();
        BuggyAList<Integer> BuggyList = new BuggyAList<>();

        for (int i = 1; i <= 3; i ++ ) {
            NomalList.addLast(i);
            BuggyList.addLast(i);
        }

        assertEquals(NomalList.size(), BuggyList.size());
        for (int i = 1; i <= 3; i ++ )
            assertEquals(NomalList.removeLast(), BuggyList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BuggyL = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BuggyL.addLast(randVal);
                assertEquals(L.size(), BuggyL.size());
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int Buggysize = BuggyL.size();
                assertEquals(size, Buggysize);
            } else if (operationNumber == 2) {
                if (L.size() == 0) continue;
                assertEquals(L.getLast(), BuggyL.getLast());
            } else if (operationNumber == 3) {
                if (L.size() == 0) continue;
                assertEquals(L.removeLast(), BuggyL.removeLast());
            }
        }
    }
}
