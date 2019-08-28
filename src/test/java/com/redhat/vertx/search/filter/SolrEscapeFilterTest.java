package com.redhat.vertx.search.filter;

import java.util.HashMap;

import com.redhat.vertx.pipeline.templates.JinjaTemplateProcessor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SolrEscapeFilterTest {

    // Running the test through the
    @Test
    public void testFilter() {
        var processor = new JinjaTemplateProcessor();
        var testString = "[Sphinx] :of (black) +{quartz}^, \" * ? ~ + | / \\ judge my & vow!";
        var expectedString = "\\[Sphinx\\] \\:of \\(black\\) \\+\\{quartz\\}\\^, \\\" \\* \\? \\~ \\+ \\| \\/ \\\\ judge my & vow\\!";

        var context = new HashMap<String, Object>();
        context.put("test", testString);

        assertThat(processor.applyTemplate(context, "{{ test | solr_escape }}")).isEqualTo(expectedString);
    }
}
