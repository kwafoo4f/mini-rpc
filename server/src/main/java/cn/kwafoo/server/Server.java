package cn.kwafoo.server;

import ch.qos.logback.classic.util.ContextInitializer;
import cn.kwafoo.coder.JsonDecoder;
import cn.kwafoo.coder.JsonEncoder;
import cn.kwafoo.handler.HeartBeatHandler;
import cn.kwafoo.handler.ServerDispatcherHandler;
import cn.kwafoo.node.NodeManager;
import cn.kwafoo.processor.Initial;
import cn.kwafoo.processor.ServerInitial;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class Server {

    /**
     * 创建server
     *
     * @return
     */
    public static Server createServer() {

        Server server = new Server();

        try {
            log.info("createServer initial...");
            // 日志配置文件加载
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "logback.xml");
            // 定时任务初始化
            Initial initial = new ServerInitial();
            initial.initial();
            log.info("createServer initial success");
        } catch (Exception e) {
            log.error("createServer err ",e);
            System.exit(-1);
        }

        log.info("create server");
        return server;
    }

    /**
     * 启动server
     *
     * @param port
     * @throws InterruptedException
     */
    public void start(int port) throws InterruptedException {
        // boos+worker 线程池组
        EventLoopGroup boos = new NioEventLoopGroup(1, new DefaultThreadFactory("boos"));
        EventLoopGroup worker = new NioEventLoopGroup(new DefaultThreadFactory("worker"));

        // 注册关闭回调
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                NodeManager.shutdown();
                // 关闭两个线程组
                boos.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (Exception e) {
                log.error("shutdown err",e);
            }
        }));


        try {
            // bootstrapServer
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boos, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 编码解码,粘包半包问题解决
                            pipeline.addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast("LengthFieldPrepender", new LengthFieldPrepender(2));// 发数据时会在数据帧头部加上数据大小
                            pipeline.addLast("encoder", new JsonEncoder());
                            pipeline.addLast("decoder", new JsonDecoder());

                            // 自定义handler
                            pipeline.addLast(new HeartBeatHandler());// 心跳处理
                            pipeline.addLast(new ServerDispatcherHandler());
                        }
                    });
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            log.info("server start port: {} success!", port);
            channelFuture.sync();
            // 监听关闭事件
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭两个线程组
            boos.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }


}
