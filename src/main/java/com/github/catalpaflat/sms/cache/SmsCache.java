package com.github.catalpaflat.sms.cache;

/**
 * @author CatalpaFlat
 */
public interface SmsCache {

    void set(String key, Object value, int time);


    Object get(String key);


    void del(String key);
}
