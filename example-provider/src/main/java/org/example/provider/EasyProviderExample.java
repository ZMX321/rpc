package org.example.provider;


import org.example.common.service.UserService;
import org.example.rpc.RpcApplication;
import org.example.rpc.easy.registry.LocalRegistry;
import org.example.rpc.easy.server.HttpServer;
import org.example.rpc.easy.server.VertxHttpServer;

public class EasyProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
