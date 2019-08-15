package com.redhat.vertx.search;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

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
    @Path("/{pipeline}")
    public CompletionStage<String> get_pipeline(@PathParam("pipeline")String pipeline) {
        return service.get(pipeline);
    }
}