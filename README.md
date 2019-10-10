Searching with vertx, depends on https://github.com/ke4roh/vertx-engine/ which has better documentation. 

This is a Quarkus project.  Run with 

```
doc_root=$(pwd)/demo/docs solr_url=http://solr.example.com/ ./mvnw compile quarkus:dev
```

## Build status
[![build status](https://travis-ci.org/ke4roh/vertx-search.svg?branch=master)](https://travis-ci.org/ke4roh/vertx-search/branches)

