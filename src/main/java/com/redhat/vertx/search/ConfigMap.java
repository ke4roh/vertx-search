package com.redhat.vertx.search;

import org.eclipse.microprofile.config.Config;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Wrap the {@link Config} in a {@link Map} so that it can be made available in constant time
 * to prospective consumers of some of the contents.
 */
public class ConfigMap extends AbstractMap<String,String> {
    private final Config config;

    public ConfigMap(Config config) {
        this.config = config;
    }

    @Override
    public @NotNull Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> set = new HashSet<>();
        config.getPropertyNames().forEach(prop ->
            set.add(Map.entry(prop,get(prop)))
        );
        return Collections.unmodifiableSet(set);
    }

    @Override
    public String get(Object key) {
        try {
            return get0((String)key);
        } catch (ClassCastException | NoSuchElementException e) {
            return null;
        }
    }

    private String get0(String key) throws NoSuchElementException {
        return config.getValue(key, String.class);
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            get0((String)key);
            return true;
        } catch (ClassCastException | NoSuchElementException e) {
            return false;
        }
    }

}
