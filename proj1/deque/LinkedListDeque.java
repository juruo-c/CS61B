package deque;

public class LinkedListDeque<Item> {
    private class DequeNode {
        public Item item;
        public DequeNode next;
        public DequeNode prev;
        public DequeNode(Item i, DequeNode n) {
            item = i;
            next = n;
        }
    }
    private int size;
    private DequeNode sentinel;

    /** Construct a empty Deque */
    public LinkedListDeque() {
        size = 0;
        sentinel = new DequeNode(null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    /** Construct a nonempty Deque */
    public LinkedListDeque(Item i) {
        size = 1;
        sentinel = new DequeNode(null, null);
        DequeNode q = new DequeNode(i, null);
        sentinel.next = q;
        sentinel.prev = q;
        q.next = sentinel;
        q.prev = sentinel;
    }

    /** add item to the front of LinkedList Deque */
    public void addFirst(Item item) {
        size ++;
        // create new DequeNode q and update its prev as sentinel and next as sentinel.next
        DequeNode q = new DequeNode(item, sentinel.next);
        q.prev = sentinel;
        // update sentinel.next 's prev to q
        sentinel.next.prev = q;
        // update sentinel 's next to q
        sentinel.next = q;
    }

    /** add item to the back of LinkedList Deque */
    public void addLast(Item item) {
        size ++;
        // create new DequeNode q and update its prev as sentinel.prev and next as sentinel
        DequeNode q = new DequeNode(item, sentinel);
        q.prev = sentinel.prev;
        // update sentinel.prev 's next to q
        sentinel.prev.next = q;
        // update sentinel 's prev to q
        sentinel.prev = q;
    }

    /** judge if the Deque is empty */
    public boolean isEmpty() {
        return size == 0;
    }

    /** return the size of Deque */
    public int size() {
        return size;
    }

    /** print the item of Deque from first to last */
    public void printDeque() {
        DequeNode q = sentinel.next;
        for (int i = 0; i < size; i ++ ) {
            System.out.println(q.item + " ");
            q = q.next;
        }
        System.out.println();
    }

    /** remove and return the first item of Deque */
    public Item removeFirst() {
        if (isEmpty())
            return null;
        size --;
        Item x = sentinel.next.item;
        DequeNode q = sentinel.next;
        sentinel.next = q.next;
        q.next.prev = sentinel;
        return x;
    }

    /** remove and return the last item of Deque */
    public Item removeLast() {
        if (isEmpty())
            return null;
        size --;
        Item x = sentinel.prev.item;
        DequeNode q = sentinel.prev;
        sentinel.prev = q.prev;
        q.prev.next = sentinel;
        return x;
    }

    /** return the item at the given index(from 0) (using iteration)*/
    public Item get(int index) {
        if (index >= size() || index < 0)
            return null;
        DequeNode q = sentinel.next;
        while (index != 0) {
            q = q.next;
            index --;
        }
        return q.item;
    }

    private Item getRecursiveHelper(DequeNode q, int index) {
        if (index == 0)
            return q.item;
        return getRecursiveHelper(q.next, index - 1);
    }

    /** return the item at the given index(from 0) (using recursion) */
    public Item getRecursive(int index) {
        if (index >= size() || index < 0)
            return null;
        return getRecursiveHelper(sentinel.next, index);
    }

//    public Iterator<Item> iterator() {
//
//    }

    /** return whether or not the parameter o is equal to the Deque */
    public boolean equals(Object o) {
        return true;
    }
}
