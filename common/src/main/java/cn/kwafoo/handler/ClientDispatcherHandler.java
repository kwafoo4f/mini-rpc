package cn.kwafoo.handler;

import cn.kwafoo.constants.MsgType;
import cn.kwafoo.processor.ProcessorManager;
import cn.kwafoo.vo.AbstractRequest;
import cn.kwafoo.vo.AsyncRequest;
import cn.kwafoo.vo.RpcVo;
import cn.kwafoo.vo.SyncRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class ClientDispatcherHandler extends SimpleChannelInboundHandler<RpcVo> {
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final ScheduledExecutorService SAY_EXECUTOR = Executors.newScheduledThreadPool(1,
            new DefaultThreadFactory("say hi"));


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcVo rpcVo) throws Exception {
        //log.info("Inbound-收到消息 {}", rpcVo);
        AbstractRequest request = AbstractRequest.respMap.get(rpcVo.getReqId());
        if (request != null) {
            request.setResp(rpcVo);
            request.finish();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive {}", ctx.channel());

        // 开启定时任务,10s中发一次数据
        SAY_EXECUTOR.scheduleAtFixedRate(()->{
            if (atomicInteger.incrementAndGet() % 2 == 0) {
                say(ctx);
            } else {
                asyncSay(ctx);
            }
            },5,10, TimeUnit.SECONDS);
    }

    private static void say(ChannelHandlerContext ctx) {
        for (int i = 0; i < 10; i++) {
            RpcVo rpcVo = new RpcVo();
            rpcVo.setType(MsgType.MSG_REQ.getCode());
            rpcVo.setMsgId(UUID.randomUUID().toString());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("code","200");
            hashMap.put("mes","HI"+i);
            rpcVo.setBody(hashMap);
            // 同步处理
            log.info("[同步请求] {}",rpcVo);
            try {
                RpcVo resp = new SyncRequest(ctx,5L)
                        .raram(rpcVo)
                        .excute();
                log.info("[同步响应] {}",resp);
            } catch (Exception e) {
                log.error("[同步异常] ",e);
            }
        }
    }

    private static void asyncSay(ChannelHandlerContext ctx) {
        for (int i = 0; i < 1000; i++) {
            RpcVo rpcVo = new RpcVo();
            rpcVo.setType(MsgType.MSG_REQ.getCode());
            rpcVo.setMsgId(UUID.randomUUID().toString());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("code","200");
            hashMap.put("mes","HI"+i);
            rpcVo.setBody(hashMap);
            // 异步处理
            log.info("[异步请求] {}",rpcVo);
            try {
                new AsyncRequest(ctx)
                        .raram(rpcVo)
                        .callback(new AsyncRequest.CallBack() {
                            @Override
                            public void success(RpcVo result) {
                                log.info("[异步响应] {}",result);
                            }

                            @Override
                            public void exception(Integer code, String cause) {
                                log.error("[异步异常] code={},cause={}",code,cause);
                            }
                        })
                        .excute();
            } catch (Exception e) {
                log.error("[异步异常] ",e);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive {}", ctx.channel());
        super.channelInactive(ctx);
    }
}
