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
        assertThat(map.get("file.separator")).isEqualTo(System.getProperties().get("file.separator"));
    }
}
