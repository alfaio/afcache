package io.github.alfaio.afcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Stream;

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

    // ================== 3、set end ==================

    // ================== 4、hash ==================
    public Integer hset(String key, String[] hkeys, String[] hvals) {
        if (hkeys == null || hvals == null || hkeys.length == 0 || hvals.length == 0 || hkeys.length != hvals.length) {
            return 0;
        }
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashMap<>());
            map.put(key, entry);
        }
        LinkedHashMap<String, String> hash = entry.getValue();
        for (int i = 0; i < hkeys.length; i++) {
            hash.put(hkeys[i], hvals[i]);
        }
        return hkeys.length;
    }

    public String hget(String key, String hkey) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().get(hkey);
    }

    public String[] hgetall(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashMap<String, String> hash = entry.getValue();
        return hash.entrySet().stream()
                .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                .toArray(String[]::new);
    }

    public String[] hmget(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashMap<String, String> hash = entry.getValue();
        return hkeys == null ? new String[0] : Arrays.stream(hkeys)
                .map(hash::get).toArray(String[]::new);

    }

    public Integer hlen(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashMap<String, String> hash = entry.getValue();
        return hash.size();
    }

    public Integer hexists(String key, String hkey) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashMap<String, String> hash = entry.getValue();
        return hash.containsKey(hkey) ? 1 : 0;
    }

    public Integer hdel(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashMap<String, String> hash = entry.getValue();
        return hkeys == null ? 0 : (int) Arrays.stream(hkeys)
                .map(hash::remove).filter(Objects::nonNull).count();
    }

    // ================== 4、hash end ==================

    // ================== 5、zset ==================

    public Integer zadd(String key, String[] vals, double[] scores) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            map.put(key, entry);
        }
        LinkedHashSet<ZsetEntry> zset = entry.getValue();
        for (int i = 0; i < vals.length; i++) {
            zset.add(new ZsetEntry(vals[i], scores[i]));
        }
        return vals.length;

    }

    public Integer zcard(String key) {
        CacheEntry<?> entry = map.get(key);
        if (entry == null) return null;
        LinkedHashSet<?> set = (LinkedHashSet<?>) entry.getValue();
        return set.size();

    }

    public Integer zcount(String key, double min, double max) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<ZsetEntry> zset = entry.getValue();
        return (int) zset.stream().filter(z -> z.getScore() >= min && z.getScore() <= max).count();
    }

    public Double zscore(String key, String val) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<ZsetEntry> zset = entry.getValue();
        return zset.stream().filter(z -> z.getValue().equals(val)).map(ZsetEntry::getScore)
                .findFirst().orElse(null);
    }

    public Integer zrank(String key, String val) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<ZsetEntry> zset = entry.getValue();
        Double zscore = zscore(key, val);
        if (zscore == null) return null;
        return (int) zset.stream().filter(z -> z.getScore() < zscore).count();
    }

    public Integer zrem(String key, String[] vals) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return 0;
        LinkedHashSet<ZsetEntry> zset = entry.getValue();
        return vals == null ? 0 : (int) Arrays.stream(vals)
                .map(x -> zset.removeIf(y -> y.getValue().equals(x)))
                .filter(x -> x).count();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheEntry<T> {
        T value;
    }

    @Data
    @AllArgsConstructor
    public static class ZsetEntry {
        private String value;
        private double score;
    }
}