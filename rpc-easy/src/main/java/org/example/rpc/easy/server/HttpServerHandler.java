package org.example.rpc.easy.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.example.rpc.easy.model.RpcRequest;
import org.example.rpc.easy.model.RpcResponse;
import org.example.rpc.easy.registry.LocalRegistry;
import org.example.rpc.easy.serializer.JdkSerializer;
import org.example.rpc.easy.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest>{
    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = new JdkSerializer();

        System.out.println("Received request: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                Class<?> impleClass = LocalRegistry.get(rpcRequest.getServiceName());

                Method method = impleClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(impleClass.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());

                rpcResponse.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
