package com.alibaba.dubbo.common;

import java.io.Serializable;

/**
 * Created by yantingxin on 2017/6/23.
 */
public class Pair<K, V> implements Serializable {
    private static final long serialVersionUID = -5167936052302981511L;

    private K key;

    private V value;

    public Pair() {

    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
