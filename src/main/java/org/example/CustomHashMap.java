package org.example;

import java.util.*;

public class CustomHashMap<K,V> {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        CustomHashMap.Node<K,V> next;

        Node(int hash, K key, V value, CustomHashMap.Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey()        { return key; }
        public V getValue()      { return value; }
        public String toString() { return key + "=" + value; }

        public int hashCode() {
            return Objects.hashCode(key) & Objects.hashCode(value);
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }
    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : key.hashCode() >>> 16 & key.hashCode();
    }
    static int tableSizeFor(int cap) {
        cap = cap - 1;

        // Находим количество битов в числе
        int numBits = 32 - Integer.numberOfLeadingZeros(cap);

        // Получаем 2^numBits
        int mask = (1 << numBits);

        return Math.min(mask, MAXIMUM_CAPACITY);
    }
    CustomHashMap.Node<K,V>[] table;
    int size;
    int threshold;
    final float loadFactor;
    public CustomHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
    public CustomHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    public CustomHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }
    public CustomHashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (table == null) {
                double dt = Math.ceil(s / (double)loadFactor);
                int t = ((dt < (double)MAXIMUM_CAPACITY) ?
                        (int)dt : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);
            } else {
                while (s > threshold && table.length < MAXIMUM_CAPACITY)
                    resize();
            }

            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value);
            }
        }
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public V get(Object key) {
        CustomHashMap.Node<K,V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }
    CustomHashMap.Node<K,V> getNode(Object key) {
        CustomHashMap.Node<K,V> first, e;
        int tableLength, hash;
        K k;
        if (table != null && (tableLength = table.length) > 0 &&
                (first = table[(tableLength - 1) & (hash = hash(key))]) != null) { //чаще всего 1 элемент в ячейке
            if (first.hash == hash &&
                    ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }
    public V put(K key, V value) {
        return putVal(hash(key), key, value);
    }
    V putVal(int hash, K key, V value) {
        CustomHashMap.Node<K,V>[] tab;
        CustomHashMap.Node<K,V> p;
        int tableLength, i;
        if ((tab = table) == null || (tableLength = tab.length) == 0) // создаем таблицу при первом добавлении
            tableLength = (tab = resize()).length;
        if ((p = tab[i = (tableLength - 1) & hash]) == null) // ячейка пустая
            tab[i] = newNode(hash, key, value, null);
        else {
            CustomHashMap.Node<K,V> e; K k;
            if (p.hash == hash && // первый элемент совпал
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else { // идем по цепочке
                while (true) {
                    e = p.next;
                    if (e == null) {
                        p.next = newNode(hash, key, value, null);
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // такой ключ существует
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        if (++size > threshold)
            resize();
        return null;
    }
    CustomHashMap.Node<K,V>[] resize() {
        CustomHashMap.Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // увеличиваем вдвое
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        CustomHashMap.Node<K,V>[] newTab = (CustomHashMap.Node<K,V>[])new CustomHashMap.Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                CustomHashMap.Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else { // preserve order
                        CustomHashMap.Node<K,V> loHead = null, loTail = null;
                        CustomHashMap.Node<K,V> hiHead = null, hiTail = null;
                        CustomHashMap.Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m, true);
    }
    public V remove(Object key) {
        CustomHashMap.Node<K,V> e;
        return (e = removeNode(hash(key), key)) == null ?
                null : e.value;
    }
    CustomHashMap.Node<K,V> removeNode(int hash, Object key) {
        CustomHashMap.Node<K,V>[] tab; CustomHashMap.Node<K,V> p; int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            CustomHashMap.Node<K,V> node = null, e; K k; V v;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key ||
                                    (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
            if (node != null ) {
                if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                --size;
                return node;
            }
        }
        return null;
    }
    public void clear() {
        CustomHashMap.Node<K,V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            Arrays.fill(tab, null);
        }
    }
    public boolean containsValue(Object value) {
        CustomHashMap.Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (CustomHashMap.Node<K,V> e : tab) {
                for (; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                            (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                keySet.add(e.key);
            }
        }
        return keySet;
    }
    public Collection<V> values() {
        Collection<V> values = new ArrayList<V>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                values.add(e.value);
            }
        }
        return values;
    }
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> keySet = new HashSet<>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                keySet.add(e);
            }
        }
        return keySet;
    }
    CustomHashMap.Node<K,V> newNode(int hash, K key, V value, CustomHashMap.Node<K,V> next) {
        return new CustomHashMap.Node<>(hash, key, value, next);
    }
}
