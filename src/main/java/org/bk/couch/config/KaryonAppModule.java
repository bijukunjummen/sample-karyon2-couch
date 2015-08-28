package org.bk.couch.config;

import io.netty.buffer.ByteBuf;
import netflix.karyon.transport.http.KaryonHttpModule;
import org.bk.couch.common.LoggingInterceptor;


public class KaryonAppModule extends KaryonHttpModule<ByteBuf, ByteBuf> {

    public KaryonAppModule() {
        super("routerModule", ByteBuf.class, ByteBuf.class);
    }

    @Override
    protected void configureServer() {
        bindRouter().toProvider(AppRouteProvider.class);

        interceptorSupport().forUri("/*").intercept(LoggingInterceptor.class);

        server().port(8888);
    }
}