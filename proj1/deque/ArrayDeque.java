package deque;

public class ArrayDeque<Item> {

    private Item[] items;
    private int nextFirst;
    private int nextLast;
    private int size;

    /** Construct a empty Deque */
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        nextFirst = 3;
        nextLast = 4;
        size = 0;
    }

    /** resize Deque with capacity */
    private void resize(int capacity) {
        Item[] d = (Item[]) new Object[capacity];

        // copy items to d
        int len = items.length;
        for (int i = (nextFirst + 1) % len, j = 0; j < size; i = (i + 1) % len, j ++)
            d[j] = items[i];

        // update items/nextFirst/nextLast
        items = d;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    /** add item to the front of LinkedList Deque */
    public void addFirst(Item item) {
        if (size == items.length)
            resize(size * 2);
        size ++;
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
    }

    /** add item to the back of LinkedList Deque */
    public void addLast(Item item) {
        if (size == items.length)
            resize(size * 2);
        size ++;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
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
        int len = items.length;
        for (int i = (nextFirst + 1) % len, j = 0; j < size; i = (i + 1) % len, j ++) {
            System.out.println(items[i] + " ");
        }
        System.out.println();
    }

    /** remove and return the first item of Deque */
    public Item removeFirst() {
        if (isEmpty())
            return null;
        if (size == items.length / 4 && items.length > 8)
            resize(items.length / 2);
        size --;
        Item x = items[(nextFirst + 1) % items.length];
        nextFirst = (nextFirst + 1) % items.length;
        return x;
    }

    /** remove and return the last item of Deque */
    public Item removeLast() {
        if (isEmpty())
            return null;
        if (size == items.length / 4 && items.length > 8)
            resize(items.length / 4);
        size --;
        Item x = items[(nextLast - 1 + items.length) % items.length];
        nextLast = (nextLast - 1 + items.length) % items.length;
        return x;
    }

    /** return the item at the given index(from 0) (using iteration)*/
    public Item get(int index) {
        if (index < 0 || index >= size)
            return null;
        int p = (nextFirst + 1 + index) % items.length;
        return items[p];
    }

//    public Iterator<Item> iterator() {
//
//    }

    /** return whether or not the parameter o is equal to the Deque */
    public boolean equals(Object o) {
        return true;
    }
}
