package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {

    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int size;

    /** Construct a empty Deque */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = 3;
        nextLast = 4;
        size = 0;
    }

    /** resize Deque with capacity */
    private void resize(int capacity) {
        T[] d = (T[]) new Object[capacity];

        // copy items to d
        int len = items.length;
        for (int i = (nextFirst + 1) % len, j = 0; j < size; i = (i + 1) % len, j++) {
            d[j] = items[i];
        }

        // update items/nextFirst/nextLast
        items = d;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    /** add item to the front of LinkedList Deque */
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        size++;
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
    }

    /** add item to the back of LinkedList Deque */
    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        size++;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
    }

    /** return the size of Deque */
    @Override
    public int size() {
        return size;
    }

    /** print the item of Deque from first to last */
    @Override
    public void printDeque() {
        int len = items.length;
        for (int i = (nextFirst + 1) % len, j = 0; j < size; i = (i + 1) % len, j++) {
            System.out.println(items[i] + " ");
        }
        System.out.println();
    }

    /** remove and return the first item of Deque */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size == items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        size--;
        T x = items[(nextFirst + 1) % items.length];
        nextFirst = (nextFirst + 1) % items.length;
        return x;
    }

    /** remove and return the last item of Deque */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size == items.length / 4 && items.length > 8) {
            resize(items.length / 4);
        }
        size--;
        T x = items[(nextLast - 1 + items.length) % items.length];
        nextLast = (nextLast - 1 + items.length) % items.length;
        return x;
    }

    /** return the item at the given index(from 0) (using iteration)*/
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int p = (nextFirst + 1 + index) % items.length;
        return items[p];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wisPos = 1;
        @Override
        public boolean hasNext() {
            return wisPos <= size;
        }

        @Override
        public T next() {
            T nextItem = items[(nextFirst + wisPos) % items.length];
            wisPos += 1;
            return nextItem;
        }
    }

    /** return whether or not the parameter o is equal to the Deque */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
