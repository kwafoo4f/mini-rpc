package cn.kwafoo;

import cn.kwafoo.server.Server;

/**
 * @description: 服务端启动类
 * @author: kwafoo
 * @date: 2024-01-10
 */
public class ServerStartup {
    public static void main(String[] args) throws InterruptedException {
        Server server = Server.createServer();
        server.start(8086);
    }
}
