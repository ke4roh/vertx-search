package com.redhat.vertx.search.filter;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StopwordsFilterTest {
    @Test
    public void testStopwordsFilter() {
        JinjavaConfig.Builder builder = JinjavaConfig.newBuilder();
        builder.withFailOnUnknownTokens(true);
        JinjavaConfig config = builder.build();
        Jinjava jinjava = new Jinjava(config);
        jinjava.getGlobalContext().registerFilter(new StopwordsFilter());

        // NLTK stopwords from https://gist.github.com/sebleier/554280
        List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");
        Map<String,Object> context = new HashMap<>();

        context.put("stopwords",stopwords);

        String result = jinjava.render("{{\"Now is the time for all good people to come to the aid of their country.\" | remove_stopwords(stopwords) }}", context);

        assertThat(result).isEqualTo("   time   good people  come   aid   country.");
    }
}
