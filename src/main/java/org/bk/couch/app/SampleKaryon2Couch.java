package org.bk.couch.app;

import com.netflix.governator.guice.BootstrapModule;
import netflix.karyon.Karyon;
import org.bk.couch.governator.SampleKaryon2CouchGovernator;

public class SampleKaryon2Couch {

    public static void main(String[] args) {
        Karyon.forApplication(SampleKaryon2CouchGovernator.class, (BootstrapModule[]) null)
                .startAndWaitTillShutdown();
    }
}
