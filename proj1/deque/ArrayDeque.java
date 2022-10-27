package deque;

public class ArrayDeque<Item> {

    private Item[] items;
    private int nextFirst;
    private int nextLast;

    /** Construct a empty Deque */
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        nextFirst = 3;
        nextLast = 4;
    }

    /** resize Deque with capacity */
    private void resize(int capacity) {
        Item[] d = (Item[]) new Object[capacity];
        System.arraycopy(items, nextFirst + 1, d )
    }

    /** add item to the front of LinkedList Deque */
    public void addFirst(Item item) {

    }

    /** add item to the back of LinkedList Deque */
    public void addLast(Item item) {

    }

    /** judge if the Deque is empty */
    public boolean isEmpty() {
        return true;
    }

    /** return the size of Deque */
    public int size() {
        return 0;
    }

    /** print the item of Deque from first to last */
    public void printDeque() {

    }

    /** remove and return the first item of Deque */
    public Item removeFirst() {
        return null;
    }

    /** remove and return the last item of Deque */
    public Item removeLast() {
        return null;
    }

    /** return the item at the given index(from 0) (using iteration)*/
    public Item get(int index) {
        return null;
    }

//    public Iterator<Item> iterator() {
//
//    }

    /** return whether or not the parameter o is equal to the Deque */
    public boolean equals(Object o) {
        return true;
    }
}
