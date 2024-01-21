package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {
    private static final Integer[] KEYS = {
            1223536, 1223536, 1678536, 1223736, 197536, 457456, 346978,
            9084532, 47732556, 125567, -656454, 235, 0, 1
    };
    private static final Integer VALUE = 12;

    private static final CustomHashMap<Integer, Integer> TEST_HASH_MAP;

    static {
        TEST_HASH_MAP = new CustomHashMap<>(14);
        for(Integer key:KEYS) {
            TEST_HASH_MAP.put(key, VALUE);
        }
    }

    @Test
    void get() {
    }

    @Test
    void containsKey() {
    }

    @Test
    void put() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        for(Integer key:KEYS) {
            hashMap.put(key, VALUE);
        }
        int expected = 13;
        int actual = hashMap.size;
        assertEquals(expected, actual);
    }

    @Test
    void putAll() {
        Map<Integer, Integer> map = new HashMap<>();
        for(Integer key:KEYS) {
            map.put(key, VALUE);
        }
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>(map);
        int expected = 13;
        int actual = hashMap.size;
        assertEquals(expected, actual);
    }

    @Test
    void remove() {
        Integer actual = TEST_HASH_MAP.remove(1223536);
        Integer expected = VALUE;
        assertEquals(expected, actual);
        expected = 12;
        actual = TEST_HASH_MAP.size;
        assertEquals(expected, actual);
    }

    @Test
    void clear() {
    }

    @Test
    void containsValue() {
    }

    @Test
    void keySet() {
    }

    @Test
    void values() {
    }

    @Test
    void entrySet() {
    }
}