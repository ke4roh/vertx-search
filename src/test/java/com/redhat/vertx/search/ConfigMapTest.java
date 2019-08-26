package com.redhat.vertx.search;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigMapTest {
    @Test
    public void testConfigMap() {
        Config config = ConfigProvider.getConfig();
        Map<String, String> map = new ConfigMap(config);

        // Something in the map
        assertThat(map.get("file.separator")).isEqualTo(System.getProperties().get("file.separator"));
        assertThat(map.containsKey("file.separator")).isTrue();

        // Something not in the map
        assertThat(map.containsKey("supercalifragilisticexpealidocious")).isFalse();
        assertThat(map.get("supercalifragilisticexpealidocious")).isNull();

        // <code>this</code> certainly is not in the map, firstly because it is not a String
        assertThat(map.containsKey(this)).isFalse();
        assertThat(map.get(this)).isNull();

        // These two probably do the same thing, but they force iteration over the entry set.
        assertThat(map.entrySet().size()).isEqualTo(map.size());
    }
}
