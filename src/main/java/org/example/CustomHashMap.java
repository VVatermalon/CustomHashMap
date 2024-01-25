package org.example;

import java.util.*;
/**
 * Хэш таблица, представляющая вариацию класса {@code HashMap}.
 * Несинхронизирован, позволяет добавлять {@code null} значения и {@code null} ключ.
 * Этот класс не гарантирует упорядоченность элементов и то, что со временем порядок
 * элементов останется тем же со временем.
 *
 * <p>Класс предоставляет постоянное время выполнения основных операций
 * ({@code get} и {@code put}), предполагая что хэш функция распределеет элементы
 * равномерно среди корзин.
 *
 * <p>Сущность {@code CustomHashMap} обладает двумя параметрами, влияющими
 * на ее производительность: <i>initial capacity</i> и <i>load factor</i>.
 * <i>Capacity</i> это число корзин в хэш таблице, и <i>initial capacity</i>
 * это число корзин в момент создания таблицы. <i>Load factor</i> это мера того,
 * насколько заполненной разрешено быть хэш таблице до того, как ее размер
 * автоматически увеличится.
 * Когда число элементов хэш таблицы превысит произведение коэффициента загрузки
 * и текущей емкости, хэш таблица будет перехэширована и размер таблицы увеличится
 * примерно вдвое.
 *
 * <p>Предполагаемое число элементов в таблице и ее коэффициент загрузки должны
 * быть учтены при задании начальной емкости, чтобы минимизировать число
 * операций перехеширования. Если начальная емкость больше чем максимальное число
 * элементов деленное на коэфициент загрузки, то операция перехеширования никогда
 * не произойдет.
 *
 * @param <K> тип ключа, поддерживаемого этой таблицей
 * @param <V> тип сопоставимых значений
 *
 * @author  Скарульская Елизавета
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 */
public class CustomHashMap<K,V> {
    /**
     * Значение начальной емкости по умолчанию - должно быть степенью двойки.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * Максимальная емкость, используется, когда более большое значение
     * указывается в любом конструкторе с параметрами.
     * Должна быть степенью двойки.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * Коэффициент загрузки, используется если он не задается в конструкторе.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Сущности, которые хранятся в ячейках таблицы.
     */
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
    /**
     * Вычисляет key.hashCode() и распространяет более высокие биты
     * хэша на более низкие. Так как в таблице используется битовая маска -
     * степень двойки для вычисления текущей ячейки таблицы, то
     * значения хэшей, которые отличаются лишь старшими битами будут постоянно
     * сталкиваться.
     */
    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : key.hashCode() >>> 16 & key.hashCode();
    }
    /**
     * Возвращает степень двойки для заданной емкости.
     */
    static int tableSizeFor(int cap) {
        cap = cap - 1;

        // Находим количество битов в числе
        int numBits = 32 - Integer.numberOfLeadingZeros(cap);

        // Получаем 2^numBits
        int mask = (1 << numBits);

        return Math.min(mask, MAXIMUM_CAPACITY);
    }
    /**
     * Таблица, инициализируемая при первом использовании
     * и изменяющая размер по необходимости.
     * Ее размер после инициализации всегда является степенью двойки.
     */
    CustomHashMap.Node<K,V>[] table;
    /**
     * Число пар ключ-значение, хранимое в таблице.
     */
    int size;
    /**
     * Следующее значение размера, при котором необходимо изменить размер таблицы
     * (capacity * load factor).
     */
    int threshold;
    /**
     * Коэффициент загрузки для текущей таблицы.
     */
    final float loadFactor;
    /**
     * Создает пустую {@code CustomHashMap} с указанной начальной емкостью
     * и коэффициентом загрузки.
     *
     * @apiNote
     * Чтобы создать {@code CustomHashMap} с начальной емкостью, которая
     * вмещает ожидаемое число пар, используйте {@link #CustomHashMap(int) newCustomHashMap}.
     *
     * @param  initialCapacity начальная емкость
     * @param  loadFactor      коэффициент загрузки
     * @throws IllegalArgumentException если начальная емкость отрицательная
     * или коэффициент загрузки не положительный.
     */
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
    /**
     * Создает пустую {@code CustomHashMap} с заданной начальной емкостью
     * и со значением коэффициента загрузки по умолчанию (0.75).
     *
     * @param  initialCapacity начальная емкость
     * @throws IllegalArgumentException если начальная емкость отрицательная.
     */
    public CustomHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    /**
     * Создает пустую {@code CustomHashMap} со значением начальной емкости
     * по умолчанию (16) и со значением коэффициента загрузки (0.75).
     */
    public CustomHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }
    /**
     * Создает новую {@code CustomHashMap} с теми же элементами, что
     * и заданная {@code Map}. {@code CustomHashMap} создается со значением
     * коэффициента загрузки по умолчанию (0.75) и с начальной емкостью, достаточной
     * для вмещения всех элементов заданной {@code Map}.
     *
     * @param   m таблица, элементы которой будут размещены в текущей таблице
     * @throws  NullPointerException если указанная таблица равна null
     */
    public CustomHashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m);
    }
    /**
     * Помещает все элементы указанной таблицы в текущую таблицу.
     *
     * @param m указанная таблица.
     */
    void putMapEntries(Map<? extends K, ? extends V> m) {
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
    /**
     * Возвращает число пар ключ-значение, хранимых в этой таблице
     *
     * @return число пар ключ-значение, хранимых в этой таблице
     */
    public int size() {
        return size;
    }
    /**
     * Возвращает {@code true} если эта таблица не содержит значений.
     *
     * @return {@code true} если эта таблица не содержит значений.
     */
    public boolean isEmpty() {
        return size == 0;
    }
    /**
     * Возвращает значение, с которым связано значение текущего ключа или
     * {@code null} если эта таблица не содержит значение для этого ключа.
     *
     * <p>Возвращаемое значение {@code null} не <i>обязательно</i>
     * указывает что таблица не содержит значение для этого ключа,
     * возможно, что в таблице хранится значение {@code null} под указанным ключом.
     * {@link #containsKey containsKey} операция может быть использована для различия
     * этих ситуаций.
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {
        CustomHashMap.Node<K,V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }
    /**
     * @param key ключ
     * @return ячейку или null при отсутствии.
     */
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
    /**
     * Возвращает {@code true} если данная таблица содержит значение для
     * указанного ключа.
     *
     * @param   key   Ключ, наличие которого необходимо проверить
     * @return {@code true} если данная таблица содержит значение для
     * указанного ключа.
     */
    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }
    /**
     * Сопоставляет указанное значение с указанным ключом в таблице.
     * Если до этого в таблице хранилось значение для этого ключа, старое
     * значение заменяется.
     *
     * @param key ключ с которым сопоставляется указанное значение
     * @param value значение с которым сопоставляется указанный ключ
     * @return предыдущее значение, связанное с {@code key}, или
     *         {@code null} нет связанного значения с {@code key}.
     *         (Возврат {@code null} также может показывать, что
     *         ранее в таблице хранилось значение {@code null}
     *         связанное с ключом {@code key}.)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value);
    }
    /**
     * @param hash хэш ключа
     * @param key ключ
     * @param value значение которое необходимо разместить
     * @return предыдущее значение, или null если таковое отсутствует.
     */
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
    /**
     * Инициализирует или удваивает размер таблицы. Если null, то
     * выделяет память в соответствии со значением начальной емкости.
     * Иначе, так как используется расширение по степени двойки, то
     * элементы из каждой ячейки должны либо остаться с тем же индексом,
     * либо должны быть перемещены со смещением степени двойки в новой таблице.
     *
     * @return таблицу
     */
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
        else if (oldThr > 0)
            newCap = oldThr;
        else {
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
    /**
     * Копирует все элементы из указанной таблицы в текущую.
     * Новые элементы заменят любые элементы, хранимые до этого
     * по тем же ключам.
     *
     * @param m элементы, помещаемые в текущую таблицу
     * @throws NullPointerException если указанная таблица равна null
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m);
    }
    /**
     * Удаляет значение по указанному ключу, если оно существует.
     *
     * @param  key ключ, значение по которому должно быть удалено.
     * @return предыдущее значение, связанное с {@code key}, или
     *         {@code null} если значение отсутствует {@code key}.
     *         (Значение {@code null} также может говорить, что ранее
     *         по этому ключу хранилось значение {@code null}.)
     */
    public V remove(Object key) {
        CustomHashMap.Node<K,V> e;
        return (e = removeNode(hash(key), key)) == null ?
                null : e.value;
    }
    /**     *
     * @param hash хэш ключа
     * @param key ключ
     * @return элемент или null, если таковой отсутствует.
     */
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
    /**
     * Удаляет все элементы текущей таблицы.
     */
    public void clear() {
        CustomHashMap.Node<K,V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            Arrays.fill(tab, null);
        }
    }
    /**
     * Возвращает {@code true} если таблица хранит один или более
     * ключей, связанных с заданным значением.
     *
     * @param value значение, наличие которого необходимо проверить
     * @return {@code true} если таблица хранит один или более
     *      * ключей, связанных с заданным значением.
     */
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
    /**
     * @return Set, состоящий из ключей заданной таблицы.
     */
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                keySet.add(e.key);
            }
        }
        return keySet;
    }
    /**
     * @return Collection, состоящую из значений заданной таблицы.
     */
    public Collection<V> values() {
        Collection<V> values = new ArrayList<V>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                values.add(e.value);
            }
        }
        return values;
    }
    /**
     * @return Set, состоящий из элементов (пар ключ-значение) заданной таблицы.
     */
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> keySet = new HashSet<>(table.length);
        for (CustomHashMap.Node<K, V> e : table) {
            for (; e != null; e = e.next) {
                keySet.add(e);
            }
        }
        return keySet;
    }
    /**
     * Создает элемент таблицы
     */
    CustomHashMap.Node<K,V> newNode(int hash, K key, V value, CustomHashMap.Node<K,V> next) {
        return new CustomHashMap.Node<>(hash, key, value, next);
    }
}
