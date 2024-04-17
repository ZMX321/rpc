package org.example.rpc.easy.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.rpc.easy.model.RpcRequest;
import org.example.rpc.easy.model.RpcResponse;
import org.example.rpc.easy.serializer.JdkSerializer;
import org.example.rpc.easy.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Serializer serializer = new JdkSerializer();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse.getData();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
