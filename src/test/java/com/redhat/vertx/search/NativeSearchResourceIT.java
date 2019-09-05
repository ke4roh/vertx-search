package com.redhat.vertx.search;

import io.quarkus.test.junit.SubstrateTest;
import org.junit.jupiter.api.Disabled;

@SubstrateTest
@Disabled("Until we actually need a native test")
public class NativeSearchResourceIT extends SearchResourceTest {

    // Execute the same tests but in native mode.
}
