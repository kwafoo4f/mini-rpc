package cn.kwafoo.client;

import ch.qos.logback.classic.util.ContextInitializer;
import cn.kwafoo.coder.JsonDecoder;
import cn.kwafoo.coder.JsonEncoder;
import cn.kwafoo.handler.ClientDispatcherHandler;
import cn.kwafoo.handler.HeartBeatPingHandler;
import cn.kwafoo.processor.ClientInitial;
import cn.kwafoo.processor.Initial;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class Client {
    // 正常关闭
    private volatile boolean shutdown = false;

    private EventLoopGroup group;

    /**
     * 创建client
     *
     * @return client
     */
    public static Client createClient() {
        // 日志配置文件加载
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "logback.xml");

        Initial initial = new ClientInitial();
        initial.initial();

        Client client = new Client();
        log.info("create client");
        return client;
    }

    /**
     * 关闭client
     *
     * @return client
     */
    public void shutdown() throws InterruptedException {
        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * 关闭client
     *
     * @return client
     */
    public void close() throws InterruptedException {
        shutdown = true;
    }

    /**
     * 启动客户端
     *
     * @param host
     * @param port
     * @throws InterruptedException
     */
    public void start(String host, int port) throws InterruptedException {
        // group
        group = new NioEventLoopGroup(new DefaultThreadFactory("group"));

        // 注册关闭回调
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                close();
            } catch (InterruptedException e) {
                log.error("shutdown err",e);
            }
        }));

        try {
            // bootstrap + 客户端配置
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 编码解码,粘包半包问题解决
                            pipeline.addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast("LengthFieldPrepender", new LengthFieldPrepender(2));// 发数据时会在数据帧头部加上数据大小
                            pipeline.addLast("encoder", new JsonEncoder());
                            pipeline.addLast("decoder", new JsonDecoder());
                            // 自定义handler
                            pipeline.addLast(new ClientDispatcherHandler());
                        }
                    });
            // 连接服务器
            connect(bootstrap, host, port, 3,60);

            log.info("close success!");
        } finally {
            // 关闭
            shutdown();
        }
    }

    public void connect(Bootstrap bootstrap, String host, int port, int tryCount,int maxReCount) throws InterruptedException {
        int count = 1;
        int maxCount = tryCount;
        while (count <= maxCount) {
            if (count != 1) {
                log.info(">>>>>> reconnect server {}:{} try-count={}", host, port,count);
            }

            // 连接服务器
            Channel channel = null;
            try {
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                channel = channelFuture.sync().channel();
                if (channel == null) {
                    sleep(count);
                    continue;
                }

                // 连接成功重置重试次数
                count = 1;
                maxCount = maxReCount;
                log.info(">>>>> server {}:{} connect success!", host, port);
                HeartBeatPingHandler.init(channel);

                // 监听关闭事件
                channel.closeFuture().sync();
                HeartBeatPingHandler.close();
                log.info("close success!");
            } catch (Exception e) {
                log.error("connect err", e);
                sleep(count);
                count++;
            }
        }
    }

    public void sleep(int count) throws InterruptedException {
        TimeUnit.SECONDS.sleep(Math.min(count << 2,60));
    }

}
