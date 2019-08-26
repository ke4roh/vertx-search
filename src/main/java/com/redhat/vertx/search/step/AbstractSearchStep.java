package com.redhat.vertx.search.step;

import com.redhat.vertx.pipeline.AbstractStep;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@Dependent
public class AbstractSearchStep extends AbstractStep {
    @Inject
    Config config;

    @Override
    protected JsonObject getEnvironment(String uuid) {
        // TODO put config in the environment
        return super.getEnvironment(uuid);
    }
}
