package com.redhat.vertx.search.filter;

import com.hubspot.jinjava.lib.filter.Filter;
import org.kohsuke.MetaInfServices;

import java.util.HashMap;
import java.util.Map;

@MetaInfServices(Filter.class)
public class StopwordsFilter extends SynonymFilter {
    @Override
    protected Map<String, String> getSynonymMap(Object arg) {
        Map<String,String> m = new HashMap<>();
        ((Iterable<String>) arg).forEach(word ->m.put(word.toLowerCase(),""));
        return m;
    }

    @Override
    protected String prepareRegex(String s) {
        return "\\b" + s + "\\b";
    }

    @Override
    public String getName() {
        return "remove_stopwords";
    }
}
