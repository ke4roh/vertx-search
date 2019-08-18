package com.redhat.vertx.search;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

/**
 * This is a file-based web server with a twist: Files are pipelines which will execute on POSTed documents.
 * All JSON.
 *
 * POST - submit a document for processing
 * PUT - submit a new pipleine definition
 * GET - fetch a pipeline definition
 * DELETE - obliterate a pipeline definition
 */
@Path("/search")
public class SearchResource {

    @Inject
    PipelineManagerService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pipeline/{name}")
    public Single<JsonObject> get_pipeline(@PathParam("name")String name) {
        return Single.create(emitter -> {
            service.get("/tmp/" + name).subscribe(
                    s -> emitter.onSuccess(new JsonObject().put("file", name).put("contents", s)),
                    err ->
//                            emitter.tryOnError(err)
                        emitter.onSuccess(new JsonObject().put("file", name).put("error", err.getMessage()))
            );
        });
    }
}
