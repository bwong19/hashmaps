package hw8;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hash map from arbitrary keys to arbitrary values.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class HashMapLinear<K, V> implements Map<K, V> {

    // Entry pairs up a key and a value.
    private class Entry<K, V> {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }
    }

    private Entry<K, V>[] data;
    private int size;
    private int capacityIndex;
    private int[] capacities = new int[]{2, 5, 11, 23, 47, 97, 197, 397,
        797, 1597, 3203, 6421, 12853, 25717, 51437, 102877, 205759, 411527,
        823117, 1646237, 3292489, 6584983, 13169977, 26339969, 52679969,
        105359939, 210719881, 421439783, 842879579, 1685759167, 2147483647};

    /**
     * Create an empty map.
     */
    public HashMapLinear() {
        capacityIndex = 0;
        size = 0;
        this.data = (Entry<K, V>[]) new Entry[2];
    }

    // gets capacity
    private int capacity() {
        return capacities[capacityIndex];
    }

    // private hash function that takes modulus
    private int hash(K key) {
        // accounts for negative values
        return Math.abs(key.hashCode()) % this.capacity();
    }

    // rehashes and doubles table
    private void rehash() {
        if (capacityIndex > 30) {
            return;
        }
        int oldCapacity = this.capacity();

        capacityIndex++;
        Entry[] temp = new Entry[this.capacity()];
        for (int i = 0; i < oldCapacity; ++i) {
            if (this.data[i] == null) {
                continue;
            }
            int code = this.hash(this.data[i].key);
            while (temp[code] != null) {
                code = (code + 1) % capacity();
            }
            temp[code] = this.data[i];

        }
        this.data = temp;
    }

    // calculates load factor
    private double load() {
        return (double) this.size / this.capacity();
    }

    // Find entry for key k, throw exception if k is null.
    private Entry find(K k) {
        if (k == null) {
            throw new IllegalArgumentException("cannot handle null key");
        }

        // linear probing
        int init = this.hash(k);
        int code;
        for (int i = 0; i < this.capacity(); ++i) {
            code = (init + i) % this.capacity();
            if (this.data[code] == null) {
                return null;
            }
            if (k.equals(this.data[code].key)) {
                return this.data[code];
            }
        }

        return null;
    }

    // Find entry for key k, throw exception if k not mapped.
    private Entry findForSure(K k) {
        Entry e = this.find(k);
        if (e == null) {
            throw new IllegalArgumentException("cannot find key " + k);
        }
        return e;
    }

    @Override
    public void insert(K k, V v) {
        if (this.has(k)) {
            throw new IllegalArgumentException("duplicate key " + k);
        }

        // linear probing
        int init = this.hash(k);
        int code;
        for (int i = 0; i < this.capacity(); ++i) {
            code = (init + i) % this.capacity();
            if (this.data[code] == null) {
                this.data[code] = new Entry<>(k, v);
                break;
            }
        }
        size++;
        if (this.load() >= 0.5) {
            this.rehash();
        }

    }

    @Override
    public V remove(K k) {
        Entry e = this.findForSure(k);
        V v = (V) e.value;
        /*
        int init = this.hash(k);
        int code;
        for (int i = 0; i < this.capacity(); ++i) {
            code = (init + i) % this.capacity();
            if (k.equals(this.data[code].key)) {
                e.key = null;
                e.value = null;
                size--;
                return v;
            }
        }
        */
        e.key = null;
        e.value = null;
        e = null;
        size--;
        return null;
    }

    @Override
    public void put(K k, V v) {
        Entry e = this.findForSure(k);
        e.value = v;
    }

    @Override
    public V get(K k) {
        Entry e = this.findForSure(k);
        return (V) e.value;
    }

    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) != null;
    }

    @Override
    public int size() {
        return size;
    }

    // Iterator for HashMaps
    private class HashMapIterator<K, V> implements Iterator<K> {
        int i;
        int n;

        @Override
        public K next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("Cannot get next element.");
            }
            while (HashMapLinear.this.data[this.i] == null) {
                this.i++;
            }
            this.n++;
            int retEle = this.i;
            i++;
            return (K) HashMapLinear.this.data[retEle].key;
        }

        @Override
        public boolean hasNext() {
            return (this.n) < HashMapLinear.this.size;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator<K, V>();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");

        int n = 0;
        for (int i = 0; n < this.size; ++i) {
            if (this.data[i] == null) {
                continue;
            }
            if (this.data[i].key == null) {
                continue;
            }
            Entry e = this.data[i];
            if (n == this.size - 1) {
                s.append(e.key + ": " + e.value);
                break;
            }
            s.append(e.key + ": " + e.value + ", ");
            n++;
        }

        s.append("}");
        return s.toString();
    }
}
