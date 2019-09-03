package com.redhat.vertx.search;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Paths;

/**
 * This is a file-based web server with a twist: Files are pipelines which will execute on POSTed documents.
 * All JSON.
 *
 * POST - submit a document for processing
 * PUT - submit a new pipleine definition
 * GET - fetch a pipeline definition
 * DELETE - obliterate a pipeline definition
 */
@Path("/")
public class SearchResource {

    @Inject
    PipelineManagerService service;

    @ConfigProperty(name = "doc_root", defaultValue = "./docs")
    String root;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{pipeline:.*}")
    public Single<String> get_pipeline(@NotNull @PathParam("pipeline")String pipeline) {
        return Single.create(emitter -> {
            service.get(resolvePath(pipeline)).subscribe(
                    s -> emitter.onSuccess(new JsonObject().put("file", pipeline).put("contents", s).toString()),
                    err ->
//                            emitter.tryOnError(err)
                        emitter.onSuccess(new JsonObject().put("file", pipeline).put("error", err.getMessage()).toString())
            );
        });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{pipeline:.*}")
    public Single<String> post_pipeline(@NotNull @PathParam("pipeline")String pipeline, @NotNull String doc) {
        return service.run(resolvePath(pipeline),doc);
    }

    private String resolvePath(String path) {
        return Paths.get(root,path).toString();
    }
}
