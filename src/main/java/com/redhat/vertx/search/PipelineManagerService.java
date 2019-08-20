package com.redhat.vertx.search;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.vertx.Engine;
import com.redhat.vertx.pool.EnginePool;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

@ApplicationScoped
public class PipelineManagerService {
    Map<String,String> filesystem = new HashMap<>();

    private EnginePool enginePool;

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
            engineSingle.subscribe(engine -> {
                engine.execute(new JsonObject(doc)).subscribe(d -> ran.onSuccess(d.toString()));
            });
        });
    }

    private EnginePool getEnginePool() {
        if (enginePool == null) {
            enginePool = new EnginePool(this::get,vertx);
        }
        return enginePool;
    }

}
