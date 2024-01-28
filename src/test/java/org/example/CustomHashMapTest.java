package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {
    private static final Integer[] KEYS = {
            1223536, 1223536, 1678536, 1223736, 197536, 457456, 346978,
            9084532, 47732556, 125567, -656454, 235, 0, 1
    };
    private static final Integer VALUE = 12;

    private CustomHashMap<Integer, Integer> TEST_HASH_MAP;
    @BeforeEach
    void setUp() {
        TEST_HASH_MAP = new CustomHashMap<>(14);
        for(Integer key:KEYS) {
            TEST_HASH_MAP.put(key, VALUE);
        }
    }

    @Test
    void get() {
        Integer expected = VALUE;
        Integer actual = TEST_HASH_MAP.get(0);
        assertEquals(expected, actual);
    }

    @Test
    void containsKey() {
        assertTrue(TEST_HASH_MAP.containsKey(1));
    }

    @Test
    void put() {
        int expected = 13;
        int actual = TEST_HASH_MAP.size;
        assertEquals(expected, actual);
    }

    @Test
    void putIdentical() {
        Integer expected = 10;
        TEST_HASH_MAP.put(0, expected);
        Integer actual = TEST_HASH_MAP.get(0);
        assertEquals(expected, actual);
    }

    @Test
    void manyValues() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        int expected = 100000;
        IntStream.range(0, 100000).limit(expected)
                .forEach(i -> hashMap.put(i, i));
        int actual = hashMap.size;
        assertEquals(expected, actual);
        IntStream.range(0, 100000).limit(expected)
                .forEach(e->assertEquals(hashMap.get(e), e));
        IntStream.range(0, 100000).limit(expected)
                .forEach(hashMap::remove);
        assertTrue(hashMap.isEmpty());
    }

    @Test
    void manySameValues() {
        CustomHashMap<Integer, Integer> hashMap = new CustomHashMap<>();
        for (int i = 0; i < 100000; i++) {
            hashMap.put(1, 1);
        }
        assertEquals(1, hashMap.size);
        for (int i = 0; i < 100000; i++) {
            assertNull(hashMap.remove(-100));
        }
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
    void removeAbsent() {
        Integer actual = TEST_HASH_MAP.remove(-100);
        assertNull(actual);
    }


    @Test
    void clear() {
        TEST_HASH_MAP.clear();
        assertTrue(TEST_HASH_MAP.isEmpty());
    }

    @Test
    void containsValue() {
        assertTrue(TEST_HASH_MAP.containsValue(12));
    }
}