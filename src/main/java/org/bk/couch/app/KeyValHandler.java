package org.bk.couch.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.netflix.governator.annotations.AutoBindSingleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.bk.couch.domain.KeyVal;
import org.bk.couch.repository.KeyValRepository;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.Charset;


@AutoBindSingleton
public class KeyValHandler implements RequestHandler<ByteBuf, ByteBuf> {

    private final ObjectMapper objectMapper;

    private final KeyValRepository keyValRepository;


    @Inject
    public KeyValHandler(KeyValRepository keyValRepository, ObjectMapper objectMapper) {
        this.keyValRepository = keyValRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        HttpMethod requestMethod = request.getHttpMethod();
        if (requestMethod == HttpMethod.GET) {
            return handleGet(request, response);
        } else if (requestMethod == HttpMethod.POST) {
            return handlePost(request, response);
        } else if (requestMethod == HttpMethod.PUT) {
            return handlePut(request, response);
        } else if (requestMethod == HttpMethod.DELETE) {
            return handleDelete(request, response);
        }

        response.setStatus(HttpResponseStatus.BAD_REQUEST);
        return response.close();
    }


    private Observable<Void> handleGet(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        String id = request.getQueryParameters().get("id").get(0);
        return Observable.just(id)
                .flatMap(keyValRepository::get)
                .flatMap(keyVal -> {
                            try {
                                return response.writeStringAndFlush(objectMapper.writeValueAsString(keyVal));
                            } catch (Exception e) {
                                response.setStatus(HttpResponseStatus.BAD_REQUEST);
                                return response.close();
                            }
                        }
                );
    }

    private Observable<Void> handlePost(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return request.getContent().map(byteBuf -> byteBuf.toString(Charset.forName("UTF-8")))
                .map(s -> {
                    try {
                        KeyVal keyVal = objectMapper.readValue(s, KeyVal.class);
                        return keyVal;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(keyValRepository::save)
                .flatMap(keyVal -> {
                            try {
                                return response.writeStringAndFlush(objectMapper.writeValueAsString(keyVal));
                            } catch (Exception e) {
                                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                                return response.close();
                            }
                        }
                );
    }

    private Observable<Void> handlePut(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return request.getContent().map(byteBuf -> byteBuf.toString(Charset.forName("UTF-8")))
                .map(s -> {
                    try {
                        KeyVal keyVal = objectMapper.readValue(s, KeyVal.class);
                        return keyVal;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(keyValRepository::update)
                .flatMap(keyVal -> {
                            try {
                                return response.writeStringAndFlush(objectMapper.writeValueAsString(keyVal));
                            } catch (Exception e) {
                                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                                return response.close();
                            }
                        }
                );
    }

    private Observable<Void> handleDelete(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        String id = request.getQueryParameters().get("id").get(0);
        return Observable.just(id)
                .flatMap(keyValRepository::get)
                .flatMap(keyValRepository::delete)
                .flatMap(b -> {
                    try {
                        return response.writeStringAndFlush(objectMapper.writeValueAsString(b));
                    } catch (Exception e) {
                        response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                        return response.close();
                    }
                });
    }
}
