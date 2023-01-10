package bstmap;

import org.apache.commons.collections.list.AbstractSerializableListDecorator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode leftChild, rightChild;

        private BSTNode() {
            leftChild = rightChild = null;
        }

        private BSTNode(K k, V v) {
            key = k;
            value = v;
            leftChild = rightChild = null;
        }

    }
    private BSTNode root;
    private int size;

    @Override
    public void clear() {
        this.size = 0;
        this.root = null;
    }

    private boolean containsKey(BSTNode curNode, K key) {
        if (curNode == null) {
            return false;
        }
        int c = key.compareTo(curNode.key);
        if (c > 0) {
            return containsKey(curNode.rightChild, key);
        }
        else if (c < 0) {
            return containsKey(curNode.leftChild, key);
        }
        return true;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(this.root, key);
    }

    private V get(BSTNode curNode, K key) {
        if (curNode == null) {
            return null;
        }
        int c = key.compareTo(curNode.key);
        if (c > 0) {
            return get(curNode.rightChild, key);
        }
        else if (c < 0) {
            return get(curNode.leftChild, key);
        }
        return curNode.value;
    }

    @Override
    public V get(K key) {
        return get(this.root, key);
    }

    @Override
    public int size() {
        return this.size;
    }

    private BSTNode insert(BSTNode curNode, K key, V value) {
        if (curNode == null) {
            this.size ++;
            return new BSTNode(key, value);
        }
        int c = key.compareTo(curNode.key);
        if (c < 0) {
            curNode.leftChild = insert(curNode.leftChild, key, value);
        }
        else if (c > 0) {
            curNode.rightChild = insert(curNode.rightChild, key, value);
        }
        else {
            curNode.value = value;
        }
        return curNode;
    }

    @Override
    public void put(K key, V value) {
        this.root = insert(this.root, key, value);
    }

    private void buildKeySet(BSTNode curNode, Set<K> keys) {
        if (curNode != null) {
            buildKeySet(curNode.leftChild, keys);
            keys.add(curNode.key);
            buildKeySet(curNode.rightChild, keys);
        }
    }
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        buildKeySet(this.root, keys);
        return keys;
    }

    /**
     * 孩子个数为0： 修改父亲孩子为null
     * 孩子个数为1： 修改父亲孩子为该节点的孩子
     * 孩子个数为2： 找到该节点的直接前驱让直接前驱取代该节点
     */

    private BSTNode removeHelper(BSTNode curNode, K key) {
        if (curNode == null) {
            return null;
        }
        int c = key.compareTo(curNode.key);
        if (c > 0) {
            curNode.leftChild = removeHelper(curNode.leftChild, key);
        }
        else if (c < 0) {
            curNode.rightChild = removeHelper(curNode.rightChild, key);
        }
        else {
            if (curNode.leftChild == null) {
                return curNode.rightChild;
            }
            if (curNode.rightChild == null) {
                return curNode.leftChild;
            }
            // find successor and replace
            BSTNode successor = curNode.leftChild;
            while (successor.rightChild != null) {
                successor = successor.rightChild;
            }
            curNode.key = successor.key;
            curNode.value = successor.value;
            // delete successor
            removeHelper(successor, successor.key);
        }
        return curNode;
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            return null;
        }
        V v = this.get(key);
        this.size --;
        this.root = removeHelper(this.root, key);
        return v;
    }

    @Override
    public V remove(K key, V value) {
        if (!this.containsKey(key) || this.get(key) != value) {
            return null;
        }
        V v = this.get(key);
        this.size --;
        this.root = removeHelper(this.root, key);
        return v;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printHelper(this.root);
    }
    private void printHelper(BSTNode curNode) {
        if (curNode == null) {
            return;
        }
        printHelper(curNode.leftChild);
        System.out.println(curNode.key);
        System.out.println(curNode.value);
        printHelper(curNode.rightChild);
    }
}
