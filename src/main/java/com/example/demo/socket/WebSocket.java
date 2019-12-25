package com.example.demo.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: tianyong
 * @date: 2019/12/24 10:41
 * @desciption:websoket服务
 */
@Component
@ServerEndpoint("/websocket/{sid}")
public class WebSocket {


    static Log log = LogFactory.getLog(WebSocket.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static volatile int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的Socket对象。
    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<WebSocket>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:建立连接
    */
    @OnOpen
    public void open(Session session,@PathParam("sid") String sid){
        this.session = session;
        webSockets.add(this);
        addOnlineCount();   //在线人数+1
        log.info("有新窗口开始监听:"+sid+"   当前在线人数为" + getOnlineCount());
        //sendMessage("已成功建立连接!");
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:关闭连接
    */
    @OnClose
    public void close(Session session){
        webSockets.remove(this);
        subOnlineCount();   //在线人数-1
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:获取客户端发来的信息
    */
    @OnMessage
    public void message(String message){
        //收到客户端消息后调用的方法
        for (WebSocket item : webSockets) {
            item.sendMessage(message);
        }
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:向单个客户端推送消息
    */
    public void sendMessage(String message){
        try{
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:群发消息
    */
    public static void sendInfoToAll(String message){
        for (WebSocket item : webSockets) {
            //这里全部推送，单独发送需要判断sid
            item.sendMessage(message);
        }
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:异常方法
    */
    @OnError
    public void error(Session session,Throwable error){
        error.printStackTrace();
    }
    /**
      * @author: tianyong
      * @date: 2019/12/24
      * @description:在线人数操作
    */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }


}
