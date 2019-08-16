package com.redhat.vertx.search;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.vertx.pool.EnginePool;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;

@ApplicationScoped
public class PipelineManagerService {
    Map<String,String> filesystem = new HashMap<>();
    EnginePool enginePool;

    @Inject
    Vertx vertx;

    @PostConstruct
    public void init() {
//        enginePool = new EnginePool(); // TODO need to figure out the best approach for using the engine
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
        return null;
    }

}
