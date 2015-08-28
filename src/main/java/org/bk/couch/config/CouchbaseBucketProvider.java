package org.bk.couch.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CouchbaseBucketProvider implements Provider<Bucket> {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseBucketProvider.class);

    public static final String BUCKET = "keyval";


    @Override
    public Bucket get() {
        Cluster cluster = CouchbaseCluster.create();
        return cluster.openBucket(BUCKET, "");
    }


}
