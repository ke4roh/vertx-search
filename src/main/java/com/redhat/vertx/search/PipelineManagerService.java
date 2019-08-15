package com.redhat.vertx.search;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class PipelineManagerService {
    private Map<String,String> filesystem = new HashMap<>();
    private EnginePool enginePool;

    public CompletionStage<String> get(String name) {
        return CompletableFuture.supplyAsync(() -> filesystem.get(name));
    }

    public CompletionStage<String> set(String name, String pipeline) {
        return CompletableFuture.supplyAsync(() -> filesystem.put(name, pipeline));
    }

    public CompletionStage<String> run(String pipeline, String doc) {
    }

}
