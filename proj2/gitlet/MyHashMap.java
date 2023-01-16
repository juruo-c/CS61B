package gitlet;

import java.io.Serializable;
import java.util.*;

/**
 * The hashmap object implemented by myself in lab8.
 *
 * @author Yang Zheng
 */
public class MyHashMap<K, V> implements Serializable, Iterable<Pair<K, V>> {
    /** =================================== Members =================================== */
    /**
     * Node structure in MyHashMap.
     */
    private class Node implements Serializable{
        K key;
        V value;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    /** Node's bucket in the hash table. */
    private Collection<Node>[] buckets;
    private int defaultInitialSize = 16;
    private final double defaultLoadFactor = 0.75;
    private int itemNum;
    private int bucketNum;

    /** =================================== Constructors =================================== */
    /** Constructors */
    public MyHashMap() {
        buckets = createTable(defaultInitialSize);
        itemNum = 0;
        bucketNum = defaultInitialSize;
    }
    /** Constructors with initial size */
    public MyHashMap(int initialSize) {
        defaultInitialSize = initialSize;
        buckets = createTable(defaultInitialSize);
        itemNum = 0;
        bucketNum = defaultInitialSize;
    }

    /** =================================== Functions =================================== */
    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket.
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }
    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i ++ ) {
            table[i] = createBucket();
        }
        return table;
    }
    /**
     * Clear the hashmap.
     */
    public void clear() {
        buckets = createTable(defaultInitialSize);
        itemNum = 0;
        bucketNum = defaultInitialSize;
    }
    /**
     * Get the bucket index of given key.
     *
     * @param key
     * @return bucket index of given key
     */
    private int getBucketIndex(K key) {
        return Math.floorMod(key.hashCode(), bucketNum);
    }
    /**
     * Check if the hashmap contains the given key.
     *
     * @param key
     * @return if the hashmap contains the given key, return true;
     *         else return false.
     */
    public boolean containsKey(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Get the value of the given key.
     *
     * @param key
     * @return if hashmap contains the given key, return the corresponding value;
     *         else return null.
     */
    public V get(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                return x.value;
            }
        }
        return null;
    }
    /**
     * Get the size of the hashmap.
     *
     * @return the size of the hashmap
     */
    public int size() {
        return itemNum;
    }
    /**
     * Resize the hash table.
     */
    private void resizeTable() {
        Collection<Node>[] newTable = createTable(bucketNum * 2);
        for (int i = 0; i < bucketNum; i ++) {
            for (Node x : buckets[i]) {
                int newBucketIndex = Math.floorMod(x.key.hashCode(), bucketNum * 2);
                newTable[newBucketIndex].add(x);
            }
        }
        buckets = newTable;
        bucketNum *= 2;
    }
    /**
     * Put the given KV pair into the hashmap.
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        int idx = getBucketIndex(key);
        if (containsKey(key)) {
            for (Node x : buckets[idx]) {
                if (x.key.equals(key)) {
                    x.value = value;
                }
            }
        }
        else {
            buckets[idx].add(createNode(key, value));
            itemNum ++;
            if (1.0 * itemNum / bucketNum > defaultLoadFactor) {
                resizeTable();
            }
        }
    }
    /**
     * Delete the given key from hashmap.
     *
     * @param key
     */
    private void delete(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                buckets[idx].remove(x);
                break;
            }
        }
    }
    /**
     * Remove the given key if the value of it
     * in the hashmap was equal to the given value.
     *
     * @param key
     * @param value
     * @return if hashmap contains the given key and
     *         the value was equal to the given value,
     *         return the corresponding value;
     *         else return null.
     */
    public V remove(K key, V value) {
        if (!containsKey(key) || get(key) != value) {
            return null;
        }
        V v = get(key);
        delete(key);
        return v;
    }
    /**
     * Remove the given key.
     *
     * @param key
     * @return if hashmap contains the given key
     *         return the corresponding value;
     *         else return null.
     */
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V v = get(key);
        delete(key);
        return v;
    }
    /**
     * Copy a new hashmap and return
     * @return a new hashmap with the same content
     */
    public MyHashMap<K, V> copy() {
        MyHashMap<K, V> cpMap = new MyHashMap<>(this.bucketNum);
        for (int i = 0; i < bucketNum; i ++ ) {
            for (Node x : buckets[i]) {
                cpMap.put(x.key, x.value);
            }
        }
        return cpMap;
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("map: ");
        for (int i = 0; i < bucketNum; i ++ ) {
            for (Node x : buckets[i]) {
                s.append("<").append(x.key).append(",").append(x.value).append(">");
            }
        }
        return s.toString();
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return new MHMIterator();
    }

    private class MHMIterator implements Iterator<Pair<K, V>> {
        private final List<Pair<K, V>> list;
        public MHMIterator() {
            list = new ArrayList<>();
            for (Collection<Node> items : buckets) {
                for (Node x : items) {
                    list.add(new Pair<>(x.key, x.value));
                }
            }
        }
        @Override
        public boolean hasNext() {
            return list.size() != 0;
        }

        @Override
        public Pair<K, V> next() {
            return list.remove(0);
        }
    }
}
