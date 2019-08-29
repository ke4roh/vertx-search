package com.redhat.vertx.search.filter;

import java.util.Objects;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;
import org.kohsuke.MetaInfServices;

/**
 * Jinjava filter that splits text on a '.'.
 *
 * Reuses logic from StringUtils in Andreas.
 * Usage: {{ "text" | dot_word_split }}
 */
@MetaInfServices
public class DotWordSplitterFilter implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        // We need a string, if it's null, use an empty string
        var s = Objects.isNull(var) ? "" : var.toString();

        // Blank strings simply get returned
        if (s.isBlank()) {
            return s;
        }

        final var regex = "(?<!\\S)([A-Za-z]\\w*(?:\\.[A-Za-z]\\w*)*?\\.([A-Za-z]\\w*))(?!\\w|\\.)";
        return s.replaceAll(regex, "($1 OR $2)");
    }

    @Override
    public String getName() {
        return "dot_word_split";
    }
}
