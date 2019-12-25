package com.example.demo.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author: tianyong
 * @date: 2019/12/24 10:40
 * @desciption:开启WebSocket支持
 */
@Configuration
public class Configure{

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return  new ServerEndpointExporter();
    }

}
