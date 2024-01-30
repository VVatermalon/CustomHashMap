package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {
    private static final Integer[] KEYS = {
            1223536, 1223536, 1678536, 1223736, 197536, 457456, 346978,
            9084532, 47732556, 125567, -656454, 235, 0, 1
    };
    private static final Integer VALUE = 12;

    private CustomHashMap<Integer, Integer> testHashMap;

    @BeforeEach
    void setUp() {
        testHashMap = new CustomHashMap<>(14);
        for (Integer key : KEYS) {
            testHashMap.put(key, VALUE);
        }
    }

    @Test
    void get_node_key() {
        var node = testHashMap.getNode(1);
        assertEquals(1, node.getKey());

    }

    @Test
    void get_node_value() {
        var node = testHashMap.getNode(1);
        assertEquals(VALUE, node.getValue());
    }
    @Test
    void set_node_value() {
        var node = testHashMap.getNode(1);
        assertEquals(VALUE, node.setValue(null));
        assertNull(node.getValue());
        assertNull(testHashMap.get(1));
    }

    @Test()
    void create_with_illegal_capacity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CustomHashMap<>(-1));
        assertEquals("Illegal initial capacity: " + -1, exception.getMessage());
    }
    @Test()
    void create_with_illegal_load_factor() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CustomHashMap<>(1, -1));
        assertEquals("Illegal load factor: " + -1.0, exception.getMessage());
    }

    @Test()
    void create_with_capacity_more_maximum() {
        CustomHashMap<Integer, Integer> map = new CustomHashMap<>(1073741825);
        assertEquals(1<<30, map.threshold);
    }

    @Test()
    void create_from_map() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 20; i < 30; i++) {
            map.put(i, i);
        }
        CustomHashMap<Integer, Integer> customMap = new CustomHashMap<>(map);
        assertEquals(10, customMap.size);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, -656454})
    void get(int key) {
        Integer expected = VALUE;
        Integer actual = testHashMap.get(key);
        assertEquals(expected, actual);
    }

    @Test
    void get_no_existing() {
        Integer actual = testHashMap.get(500);
        assertNull(actual);
    }

    @Test
    void get_many_values() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        int count = 100000;
        IntStream.range(0, count)
                .forEach(i -> hashMap.put(i, i));
        IntStream.range(0, count)
                .forEach(e -> assertEquals(hashMap.get(e), e));
    }

    @Test
    void contains_key() {
        assertTrue(testHashMap.containsKey(1));
    }

    @Test
    void contains_key_false() {
        assertFalse(testHashMap.containsKey(-1));
    }

    @Test
    void put() {
        int expected = 13;
        int actual = testHashMap.size();
        assertEquals(expected, actual);
    }

    @Test
    void put_identical() {
        Integer expected = 10;
        testHashMap.put(0, expected);
        Integer actual = testHashMap.get(0);
        assertEquals(expected, actual);
    }

    @Test
    void put_null() {
        Integer expected = 10;
        testHashMap.put(null, expected);
        Integer actual = testHashMap.get(null);
        assertEquals(expected, actual);
    }

    @Test
    void put_many_values() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        int expected = 100000;
        IntStream.range(0, 100000).limit(expected)
                .forEach(i -> hashMap.put(i, i));
        int actual = hashMap.size;
        assertEquals(expected, actual);
    }

    @Test
    void put_many_identical_values() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        for (int i = 0; i < 100000; i++) {
            hashMap.put(1, 1);
        }
        assertEquals(1, hashMap.size);
    }

    @Test
    void put_all() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 20; i < 30; i++) {
            map.put(i, i);
        }
        int expected = testHashMap.size + 10;
        testHashMap.putAll(map);
        assertEquals(expected, testHashMap.size);
    }

    @Test
    void remove() {
        Integer actual = testHashMap.remove(1223536);
        Integer expected = VALUE;
        assertEquals(expected, actual);
        expected = 12;
        actual = testHashMap.size;
        assertEquals(expected, actual);
    }

    @Test
    void remove_when_many_in_a_bucket() {
        CustomHashMap<Integer, Integer> map = new CustomHashMap<>(10, 3);
        for (int i = 0; i < 30; i++) {
            map.put(i, i);
        }
        Integer expected = 10;
        Integer actual = map.remove(expected);
        assertEquals(expected, actual);
    }

    @Test
    void remove_many_values() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        int count = 100000;
        IntStream.range(0, count)
                .forEach(i -> hashMap.put(i, i));
        IntStream.range(0, count)
                .forEach(hashMap::remove);
        assertTrue(hashMap.isEmpty());
    }

    @Test
    void remove_many_identical_values() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        for (int i = 0; i < 100000; i++) {
            assertNull(hashMap.remove(-100));
        }
    }

    @Test
    void remove_absent() {
        Integer actual = testHashMap.remove(-100);
        assertNull(actual);
    }


    @Test
    void clear() {
        testHashMap.clear();
        assertTrue(testHashMap.isEmpty());
    }

    @Test
    void contains_value() {
        assertTrue(testHashMap.containsValue(12));
    }

    @Test
    void contains_value_false() {
        assertFalse(testHashMap.containsValue(0));
    }

    @Test
    void entry_set() {
        var entries = testHashMap.entrySet();
        assertEquals(13, entries.size());
    }

    @Test
    void values() {
        var values = testHashMap.values();
        assertEquals(13, values.size());
        assertTrue(values.stream().allMatch(VALUE::equals));
    }
    @Test
    void key_set() {
        var keys = testHashMap.keySet();
        assertEquals(13, keys.size());
    }
}