package org.bk.couch.repository;


import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.inject.Inject;
import org.bk.couch.domain.KeyVal;
import rx.Observable;

public class KeyValRepository {

    private final Bucket bucket;

    @Inject
    public KeyValRepository(Bucket bucket) {
        this.bucket = bucket;
    }

    public Observable<KeyVal> get(String id) {
        return this.bucket
                .async()
                .get(id)
                .map(this::fromJsonDocument);
    }

    public Observable<KeyVal> save(KeyVal keyVal) {
        JsonDocument doc = jsonDocumentFromKeyVal(keyVal);
        return this.bucket
                .async()
                .insert(doc)
                .map(this::fromJsonDocument);
    }

    public Observable<KeyVal> update(KeyVal keyVal) {
        JsonDocument doc = jsonDocumentFromKeyVal(keyVal);
        return this.bucket.async().upsert(doc).map(this::fromJsonDocument);
    }


    public Observable<Boolean> delete(KeyVal keyVal) {
        JsonDocument doc = jsonDocumentFromKeyVal(keyVal);
        return this.bucket.async().remove(keyVal.getKey()).map(d -> true);
    }

    private KeyVal fromJsonDocument(JsonDocument jsonDocument) {
        return new KeyVal(jsonDocument.id(), jsonDocument.content().getString("value"));
    }

    private JsonDocument jsonDocumentFromKeyVal(KeyVal keyVal) {
        JsonObject jsonObject = JsonObject.empty().put("key", keyVal.getKey()).put("value", keyVal.getValue());
        JsonDocument doc = JsonDocument.create(keyVal.getKey(), jsonObject);
        return doc;
    }


}
