package cn.kwafoo;

import cn.kwafoo.client.Client;

/**
 * @description: 客户端启动类
 * @author: kwafoo
 * @date: 2024-01-10
 */
public class ClientStartup {
    public static void main(String[] args) throws InterruptedException {
        Client.createClient().start("127.0.0.1",8086);
    }
}
