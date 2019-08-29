package com.redhat.vertx.search.filter;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.TemplateSyntaxException;
import com.hubspot.jinjava.lib.filter.AdvancedFilter;
import com.hubspot.jinjava.lib.filter.Filter;
import org.kohsuke.MetaInfServices;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MetaInfServices(Filter.class)
public class SynonymFilter implements AdvancedFilter {
    @Override
    public Object filter(Object qStr, JinjavaInterpreter interpreter, Object[] args, Map<String, Object> kwargs) {
        if ( args.length != 1 || args[0] == null) {
            throw new TemplateSyntaxException(interpreter, getName(), "requires 1 argument");
        }
        if ((!(qStr instanceof String)) || ((String) qStr).isEmpty()) {
            return qStr;
        }
        Map<String, String> translations = getSynonymMap(args[0]);

        StringBuilder patternBuilder = new StringBuilder();
        translations.keySet().stream().map(this::prepareRegex)
                .forEach(s-> { patternBuilder.append(s); patternBuilder.append('|'); } );
        patternBuilder.deleteCharAt(patternBuilder.length()-1);
        Pattern p = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher((String) qStr);
        String s = m.replaceAll(match-> translations.get(match.group().toLowerCase()));
        return s;
    }

    protected String prepareRegex(String s) {
        return Pattern.quote(s);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> getSynonymMap(Object arg) {
        Map<String,String> m = new HashMap<>();
        ((Map<String, String>) arg).entrySet().forEach(e -> m.put(e.getKey().toLowerCase(), e.getValue()));
        return m;
    }

    @Override
    public String getName() {
        return "replace_synonyms";
    }
}
