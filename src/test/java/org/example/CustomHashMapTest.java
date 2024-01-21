package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {
    private static final Integer[] KEYS = {
            1223536, 1223536, 1678536, 1223736, 197536, 457456, 346978,
            9084532, 47732556, 125567, -656454, 235, 0, 1
    };
    private static final Integer VALUE = 12;

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
    }

    @Test
    void remove() {
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