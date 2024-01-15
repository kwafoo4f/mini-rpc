package cn.kwafoo.vo;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @description: 请求
 * @author: zk
 * @date: 2024-01-10
 */
public abstract class AbstractRequest {
    public static final Map<String, AbstractRequest> respMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx;

    private RpcVo req;

    private volatile RpcVo resp;
    public AbstractRequest(){};

    public AbstractRequest(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public abstract void finish();


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public RpcVo getReq() {
        return req;
    }

    public void setReq(RpcVo req) {
        this.req = req;
    }

    public RpcVo getResp() {
        return resp;
    }

    public void setResp(RpcVo resp) {
        this.resp = resp;
    }
}
