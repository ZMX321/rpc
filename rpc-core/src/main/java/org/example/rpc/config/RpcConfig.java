package org.example.rpc.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name = "rpc";
    private String version = "1.0";
    private String serverHost = "localhost";
    private int serverPort = 8080;
    private boolean mock = false;
}
