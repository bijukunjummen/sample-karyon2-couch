package org.bk.couch.config;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.netty.buffer.ByteBuf;
import netflix.karyon.transport.http.SimpleUriRouter;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import org.bk.couch.app.KeyValHandler;
import org.bk.couch.common.health.HealthCheck;

public class AppRouteProvider implements Provider<SimpleUriRouter<ByteBuf, ByteBuf>> {

    @Inject
    private HealthCheck healthCheck;

    @Inject
    private KeyValHandler keyValHandler;

    @Override
    public SimpleUriRouter get() {
        SimpleUriRouter simpleUriRouter = new SimpleUriRouter();
        simpleUriRouter.addUri("/healthcheck", new HealthCheckEndpoint(healthCheck));
        simpleUriRouter.addUri("/keyVal", keyValHandler);
        return simpleUriRouter;
    }
}