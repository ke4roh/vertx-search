package com.redhat.vertx.search;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.vertx.Engine;
import com.redhat.vertx.pool.EnginePool;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.eclipse.microprofile.config.Config;

import java.util.Map;

@ApplicationScoped
public class PipelineManagerService {
    private EnginePool enginePool;


    @Inject
    Config config;

    @Inject
    Vertx vertx;

    @PostConstruct
    public void init() {
        getEnginePool();
    }

    public Single<String> get(String name) {
        return Single.create(emitter -> {
            vertx.fileSystem().rxReadFile(name).subscribe(
                    buffer -> emitter.onSuccess(buffer.toString()),
                    emitter::tryOnError
            );
        });
    }

    public Single<String> set(String name, String pipeline) {
        return null;
    }


    public Single<String> run(String pipeline, String doc) {
        return Single.create(ran -> {
            Single<Engine> engineSingle = getEnginePool().getEngineByPipelineName(pipeline);
            engineSingle.subscribe(
                    engine -> engine.execute(new JsonObject(doc)).subscribe(d -> ran.onSuccess(d.toString())),
                    ran::onError
            );
        });
    }

    @SuppressWarnings("unchecked")
    private EnginePool getEnginePool() {
        if (enginePool == null) {
            enginePool = new EnginePool(this::get,vertx, (Map)new ConfigMap(config));
        }
        return enginePool;
    }

}
