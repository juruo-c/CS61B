package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int defaultInitialSize = 16;
    private double defaultLoadFactor = 0.75;
    private int itemNum;
    private int bucketNum;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(defaultInitialSize);
        itemNum = 0;
        bucketNum = defaultInitialSize;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        itemNum = 0;
        bucketNum = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        defaultLoadFactor = maxLoad;
        itemNum = 0;
        bucketNum = initialSize;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i ++ ) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(defaultInitialSize);
        itemNum = 0;
        bucketNum = defaultInitialSize;
    }

    private int getBucketIndex(K key) {
        return Math.floorMod(key.hashCode(), bucketNum);
    }

    @Override
    public boolean containsKey(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                return x.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return itemNum;
    }

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

    @Override
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

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (int i = 0; i < bucketNum; i ++ ) {
            for (Node x : buckets[i]) {
                keys.add(x.key);
            }
        }
        return keys;
    }

    private void delete(K key) {
        int idx = getBucketIndex(key);
        for (Node x : buckets[idx]) {
            if (x.key.equals(key)) {
                buckets[idx].remove(x);
                break;
            }
        }
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key) || get(key) != value) {
            return null;
        }
        V v = get(key);
        delete(key);
        return v;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V v = get(key);
        delete(key);
        return v;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
