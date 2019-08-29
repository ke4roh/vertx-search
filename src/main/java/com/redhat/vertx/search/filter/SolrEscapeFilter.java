package com.redhat.vertx.search.filter;

import java.util.Objects;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;
import org.kohsuke.MetaInfServices;

/**
 * Jinjava filter to properly escape something before sending it to solr.
 *
 * The string passed in will search for the following characters: :+!^"~*?|\/()[]{}
 * Those characters are then each escaped with a '\'.
 *
 * Usage: {{ var | solr_escape }}
 * var = [Sphinx] :of (black) +{quartz}^, " * ? ~ + | / \ judge my & vow!
 * Result: \[Sphinx\] \:of \(black\) \+\{quartz\}\^, \" \* \? \~ \+ \| \/ \\ judge my & vow\!
 */
@MetaInfServices
public class SolrEscapeFilter implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        if (Objects.isNull(var) || var.toString().isBlank()) {
            return "";
        }
        var pattern = "([\\Q:+!^\"~*?|\\/()[]{}\\E])";
        final String replacement = "\\\\$1";

        return var.toString().replaceAll(pattern, replacement);
    }

    @Override
    public String getName() {
        return "solr_escape";
    }
}
