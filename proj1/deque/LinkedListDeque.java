package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>{
    private class DequeNode {
        public T item;
        public DequeNode next;
        public DequeNode prev;
        public DequeNode(T i, DequeNode n) {
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
    public LinkedListDeque(T i) {
        size = 1;
        sentinel = new DequeNode(null, null);
        DequeNode q = new DequeNode(i, null);
        sentinel.next = q;
        sentinel.prev = q;
        q.next = sentinel;
        q.prev = sentinel;
    }

    /** add item to the front of LinkedList Deque */
    public void addFirst(T item) {
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
    public void addLast(T item) {
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
    public T removeFirst() {
        if (isEmpty())
            return null;
        size --;
        T x = sentinel.next.item;
        DequeNode q = sentinel.next;
        sentinel.next = q.next;
        q.next.prev = sentinel;
        return x;
    }

    /** remove and return the last item of Deque */
    public T removeLast() {
        if (isEmpty())
            return null;
        size --;
        T x = sentinel.prev.item;
        DequeNode q = sentinel.prev;
        sentinel.prev = q.prev;
        q.prev.next = sentinel;
        return x;
    }

    /** return the item at the given index(from 0) (using iteration)*/
    public T get(int index) {
        if (index >= size() || index < 0)
            return null;
        DequeNode q = sentinel.next;
        while (index != 0) {
            q = q.next;
            index --;
        }
        return q.item;
    }

    private T getRecursiveHelper(DequeNode q, int index) {
        if (index == 0)
            return q.item;
        return getRecursiveHelper(q.next, index - 1);
    }

    /** return the item at the given index(from 0) (using recursion) */
    public T getRecursive(int index) {
        if (index >= size() || index < 0)
            return null;
        return getRecursiveHelper(sentinel.next, index);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wisPos = 0;
        private DequeNode wisNode = sentinel.next;

        @Override
        public boolean hasNext() {
            return wisPos < size;
        }

        @Override
        public T next() {
            T nextItem = wisNode.item;
            wisNode = wisNode.next;
            wisPos ++;
            return nextItem;
        }
    }

    /** return whether or not the parameter o is equal to the Deque */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof LinkedListDeque))
            return false;

        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (other.size() != this.size())
            return false;
        DequeNode q = this.sentinel.next;
        int i = 0;
        while (i < this.size()) {
            if (!(q.item.equals(other.get(i))))
                return false;
            i ++;
            q = q.next;
        }

        return true;
    }
}
