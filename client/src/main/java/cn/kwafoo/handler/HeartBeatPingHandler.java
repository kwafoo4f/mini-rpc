package cn.kwafoo.handler;

import cn.kwafoo.constants.MsgType;
import cn.kwafoo.utils.IdUtil;
import cn.kwafoo.vo.RpcVo;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: 心跳任务
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class HeartBeatPingHandler {
    private static volatile boolean closed = true;
    private static Channel channel;
    // 心跳请求定时任务
    private static final ScheduledExecutorService PING_EXECUTOR = Executors.newScheduledThreadPool(1,
            new DefaultThreadFactory("ping"));

    public static void init(Channel newChannel) {
        if (closed) {
            log.info(">>>>>> ping task init closed={}",closed);
            PING_EXECUTOR.schedule(new HeartBeatTask(),10,TimeUnit.SECONDS);
            closed = false;
        }
        Channel oldChannel = channel;
        if (oldChannel == null || oldChannel != newChannel) {
            channel = newChannel;
        }
        if (oldChannel != null) {
            oldChannel.close();
        }
    }

    public static void close() {
        log.info(">>>>>> ping task close");
        closed = true;
        if (channel != null) {
            Channel oldChannel = channel;
            channel = null;
            oldChannel.close();
        }
    }

    public static void shutdown() {
        log.info(">>>>>> ping task shutdown");
        close();
        PING_EXECUTOR.shutdown();
    }

    public static void ping() {
        log.info(">>>>>> ping {}",channel != null ? channel.remoteAddress().toString() : "null");
        if (channel != null) {
            RpcVo rpcVo = new RpcVo();
            rpcVo.setType(MsgType.HEARTBEAT_REQ.getCode());
            rpcVo.setMsgId(IdUtil.uuid());
            rpcVo.setBody("ping...");
            channel.writeAndFlush(rpcVo);
        }

    }

    private static class HeartBeatTask implements Runnable {
        @Override
        public void run() {
            if (!closed) {
                ping();
                PING_EXECUTOR.schedule(this,10,TimeUnit.SECONDS);
            }
        }
    }
}
