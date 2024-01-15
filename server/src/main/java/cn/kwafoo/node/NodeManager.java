package cn.kwafoo.node;

import cn.kwafoo.vo.NodeInfo;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public final class NodeManager {
    // 过期时间
    private static final long EXPIRE_TIME = Duration.of(30, ChronoUnit.SECONDS).toMillis();
    // 过期节点任务
    private static final ScheduledExecutorService EXPIRE_SCAN_EXECUTOR = Executors.newScheduledThreadPool(1,
            new DefaultThreadFactory("expire-scan"));
    // 节点心跳信息
    private static final Map<Channel, NodeInfo> nodeInfoMap = new ConcurrentHashMap<>();

    /**
     * put
     */
    public static void put(Channel channel, NodeInfo nodeInfo) {
        nodeInfoMap.put(channel,nodeInfo);
    }

    /**
     * createOrRefresh
     */
    public static void createOrRefresh(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress)channel.remoteAddress();
        log.info(">>>>>> remote address {} ping handle", remoteAddress);
        NodeInfo nodeInfo = nodeInfoMap.get(channel);
        if (nodeInfo != null) {
            nodeInfo.setLastTime(System.currentTimeMillis());
        } else {
            nodeInfo = new NodeInfo();
            nodeInfo.setHost(remoteAddress.getAddress().getHostAddress());
            nodeInfo.setPort(remoteAddress.getPort());
            nodeInfo.setLastTime(System.currentTimeMillis());
            put(channel,nodeInfo);
        }
    }

    /**
     * init
     */
    public static void init() {
        EXPIRE_SCAN_EXECUTOR.scheduleAtFixedRate(NodeManager::scanNoActiveNode,5,10, TimeUnit.SECONDS);
    }

    /**
     * 扫描不活跃的节点
     */
    public static void scanNoActiveNode() {
        log.debug(">>>>>>>> scanNoActiveNode-start,node-count:{}",nodeInfoMap.keySet().size());
        Iterator<Map.Entry<Channel, NodeInfo>> iterator = nodeInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Channel, NodeInfo> nodeEntry = iterator.next();
            NodeInfo nodeInfo = nodeEntry.getValue();
            // 过期判断
            if ((nodeInfo.getLastTime() + EXPIRE_TIME) < System.currentTimeMillis()) {
                // 移除nodeIno
                Channel channel = nodeEntry.getKey();
                iterator.remove();
                // 关闭channel
                closeChannel(channel);
                log.info(">>>>>>>> NodeInfo {}:{} are removed",nodeInfo.getHost(),nodeInfo.getPort());
            }
        }
        log.debug(">>>>>>>> scanNoActiveNode-end");
    }

    /**
     * 关闭Channel
     * @param channel
     */
    public static void closeChannel(Channel channel) {
        if (channel == null) {
            return;
        }
        channel.close();
    }


    public static void shutdown() {
        EXPIRE_SCAN_EXECUTOR.shutdown();
    }
}
