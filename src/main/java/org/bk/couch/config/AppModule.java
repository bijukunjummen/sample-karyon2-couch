package org.bk.couch.config;

import com.couchbase.client.java.Bucket;
import com.google.inject.AbstractModule;
import com.netflix.governator.guice.lazy.LazySingleton;

public class AppModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(Bucket.class).toProvider(CouchbaseBucketProvider.class);
    }
}
