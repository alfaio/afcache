package io.github.alfaio.afcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author LinMF
 * @since 2024/6/19
 **/
public class AfCache {

    Map<String, CacheEntry<?>> map = new HashMap<>();

    // ================== 1、string ==================
    public String get(String key) {
        CacheEntry<String> entry = (CacheEntry<String>) map.get(key);
        return entry.value;
    }

    public void set(String key, String value) {
        map.put(key, new CacheEntry<>(value));
    }

    public int del(String... keys) {
        return keys == null ? 0 : (int) Arrays.stream(keys)
                .map(map::remove).filter(Objects::nonNull).count();
    }

    public int exists(String... keys) {
        return keys == null ? 0 : (int) Arrays.stream(keys)
                .map(map::containsKey).filter(x -> x).count();
    }

    public String[] mget(String... keys) {
        return keys == null ? null : Arrays.stream(keys)
                .map(this::get).toArray(String[]::new);
    }

    public void mset(String[] keys, String[] values) {
        if (keys == null || keys.length == 0) return;
        for (int i = 0; i < keys.length; i++) {
            set(keys[i], values[i]);
        }
    }

    public int incr(String key) {
        String str = get(key);
        int val = 0;
        try {
            if (str != null) {
                val = Integer.parseInt(str);
            }
            val++;
            set(key, String.valueOf(val));
        } catch (NumberFormatException e) {
            throw e;
        }
        return val;
    }

    public int decr(String key) {
        String str = get(key);
        int val = 0;
        try {
            if (str != null) {
                val = Integer.parseInt(str);
            }
            val--;
            set(key, String.valueOf(val));
        } catch (NumberFormatException e) {
            throw e;
        }
        return val;
    }

    public Integer strLen(String key) {
        return get(key) == null ? null : get(key).length();
    }

    // ================== 1、string end ==================

    // ================== 2、list ==================
    public Integer lpush(String key, String... vals) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            map.put(key, entry);
        }
        LinkedList<String> list = entry.getValue();
        Arrays.stream(vals).forEach(list::addFirst);
        return vals.length;
    }

    public String[] lpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> list = entry.getValue();
        if (list == null) return null;

        int len = Math.min(count, list.size());
        String[] result = new String[len];
        int index = 0;
        while (index < len) {
            result[index++] = list.removeFirst();
        }
        return result;
    }

    public Integer rpush(String key, String... vals) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            map.put(key, entry);
        }
        LinkedList<String> list = entry.getValue();
        list.addAll(List.of(vals));
        return vals.length;
    }

    public String[] rpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> list = entry.getValue();
        if (list == null) return null;

        int len = Math.min(count, list.size());
        String[] result = new String[len];
        int index = 0;
        while (index < len) {
            result[index++] = list.removeLast();
        }
        return result;
    }

    public Integer llen(String key) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedList<String> list = entry.getValue();
        if (list == null) return 0;
        return list.size();
    }

    public String lindex(String key, int index) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> list = entry.getValue();
        if (list == null) return null;
        if (index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public String[] lrange(String key, int start, int end) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> list = entry.getValue();
        if (list == null) return null;
        int size = list.size();
        if (start >= size) return null;
        if (end >= size) end = size - 1;
        int len = Math.min(size, end - start + 1);
        String[] result = new String[len];
        for (int i = 0; i < len; i++) {
            result[i] = list.get(start + i);
        }
        return result;
    }

    // ================== 2、list end ==================

    // ================== 3、set ==================
    public Integer sadd(String key, String... vals) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            map.put(key, entry);
        }
        LinkedHashSet<String> set = entry.getValue();
        set.addAll(Arrays.asList(vals));
        return vals.length;
    }

    public String[] smembers(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> set = entry.getValue();
        return set.toArray(String[]::new);
    }

    public Integer scard(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> set = entry.getValue();
        return set.size();
    }

    public Integer sismember(String key, String val) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<String> set = entry.getValue();
        return set.contains(val) ? 1 : 0;
    }

    public Integer srem(String key, String[] vals) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> set = entry.getValue();
        return vals == null ? 0 : (int) Arrays.stream(vals)
                .map(set::remove).filter(x -> x).count();
    }

    Random random = new Random();

    public String[] spop(String key, int count) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> set = entry.getValue();
        if (set == null) return null;

        int len = Math.min(count, set.size());
        String[] result = new String[len];
        int index = 0;
        while (index < len) {
            String[] array = set.toArray(String[]::new);
            String obj = array[random.nextInt(array.length)];
            set.remove(obj);
            result[index++] = obj;
        }
        return result;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheEntry<T> {
        T value;
    }
}