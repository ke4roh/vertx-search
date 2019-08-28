package com.redhat.vertx.search.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.TemplateSyntaxException;
import com.hubspot.jinjava.lib.filter.Filter;
import org.kohsuke.MetaInfServices;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MetaInfServices(Filter.class)
public class SynonymFilter implements Filter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Object filter(Object qStr, JinjavaInterpreter interpreter, String... args) {
        if ( args.length != 1 ) {
            throw new TemplateSyntaxException(interpreter, getName(), "requires 1 argument (a map of synonyms)");
        }
        if ((!(qStr instanceof String)) || ((String) qStr).isEmpty()) {
            return qStr;
        }

        // TODO figure out a more robust way to receive a paramater indicating a map of synonyms
        /**
         * Jinja is resolving the name of the variable representing a map to its toString() value
         * which does not include quotes around the keys and values.
         */
        Map<String,String> translations=new HashMap();
        String params = args[0].substring(1,args[0].length()-1);
        for (String kvp: params.split(", ")) {
            String[] kv = kvp.split("=");
            translations.put(kv[0].toLowerCase(),kv[1]);
        }

        StringBuilder patternBuilder = new StringBuilder();
        translations.keySet().stream().map(Pattern::quote)
                .forEach(s-> { patternBuilder.append(s); patternBuilder.append('|'); } );
        patternBuilder.deleteCharAt(patternBuilder.length()-1);
        Pattern p = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher((String) qStr);
        String s = m.replaceAll(match-> translations.get(match.group().toLowerCase()));
        return s;
    }

    @Override
    public String getName() {
        return "replace_synonyms";
    }
}
