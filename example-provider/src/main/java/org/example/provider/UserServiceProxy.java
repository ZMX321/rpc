package org.example.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.common.model.User;
import org.example.common.service.UserService;
import org.example.rpc.easy.model.RpcRequest;
import org.example.rpc.easy.model.RpcResponse;
import org.example.rpc.easy.serializer.JdkSerializer;
import org.example.rpc.easy.serializer.Serializer;

import java.io.IOException;

//static proxy
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{User.class})
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
