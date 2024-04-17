package org.example.consumer;

import org.example.common.model.User;
import org.example.common.service.UserService;
import org.example.rpc.easy.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {


        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("test");

        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
