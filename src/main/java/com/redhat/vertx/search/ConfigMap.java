package com.redhat.vertx.search;

import org.eclipse.microprofile.config.Config;

import java.util.*;

/**
 * Wrap the {@link Config} in a {@link Map} so that it can be made available in constant time
 * to prospective consumers of some of the contents.
 */
public class ConfigMap extends AbstractMap<String,Object> {
    private final Config config;

    public ConfigMap(Config config) {
        this.config = config;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<>();
        for (String prop : config.getPropertyNames()) {
            set.add(Map.entry(prop,get(prop)));
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Object get(Object key) {
        if (! (key instanceof String)) {
            return null;
        }
        return config.getValue((String)key, Object.class);
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String) && (config.getValue((String)key,Object.class)!=null);
    }

}
