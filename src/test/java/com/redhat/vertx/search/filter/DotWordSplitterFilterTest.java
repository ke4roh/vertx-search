package com.redhat.vertx.search.filter;

import java.util.Collections;
import java.util.HashMap;

import com.redhat.vertx.pipeline.templates.JinjaTemplateProcessor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DotWordSplitterFilterTest {
    final JinjaTemplateProcessor processor = new JinjaTemplateProcessor();

    @Test
    public void basicTest() {
        var testString = "java.lang.NullPointerException";
        var expectedString = "(java.lang.NullPointerException OR NullPointerException)";

        assertThat(
                processor.applyTemplate(Collections.emptyMap(), "{{ \"" + testString + "\" | dot_word_split }}"))
                .isEqualTo(expectedString);
    }

    @Test
    public void sentenceTest() {
        var testString = "Mary had a little lamb it's hiera.yaml as white as snow";
        var expectedString = "Mary had a little lamb it's (hiera.yaml OR yaml) as white as snow";

        assertThat(
                processor.applyTemplate(Collections.emptyMap(), "{{ \"" + testString + "\" | dot_word_split }}"))
                .isEqualTo(expectedString);
    }

    @Test
    public void nullTest() {
        String expectedString = "";

        var context = new HashMap<String, Object>();
        context.put("test", null);

        assertThat(
                processor.applyTemplate(context, "{{ test | dot_word_split }}"))
                .isEqualTo(expectedString);
    }

    @Test
    public void blankTest() {
        var testString = " ";
        var expectedString = " ";

        assertThat(
                processor.applyTemplate(Collections.emptyMap(), "{{ \"" + testString + "\" | dot_word_split }}"))
                .isEqualTo(expectedString);
    }

    @Test
    public void numberStringTest() {
        var testString = "123.456";
        var expectedString = "123.456";

        assertThat(
                processor.applyTemplate(Collections.emptyMap(), "{{ \"" + testString + "\" | dot_word_split }}"))
                .isEqualTo(expectedString);
    }

    @Test
    public void notAllowedCharactersTest() {
        var testString = "abc.123.not$%^allowed.blah";
//        var testString = "com.redhat.vertx.engine.AbstractStep$Concat";
        var expectedString = "abc.123.not$%^allowed.blah";

        assertThat(
                processor.applyTemplate(Collections.emptyMap(), "{{ \"" + testString + "\" | dot_word_split }}"))
                .isEqualTo(expectedString);
    }
}
