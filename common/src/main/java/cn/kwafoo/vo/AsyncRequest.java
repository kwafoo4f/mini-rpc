package cn.kwafoo.vo;

import io.netty.channel.ChannelHandlerContext;

/**
 * @description: 异步请求
 * @author: zk
 * @date: 2024-01-10
 */
public final class AsyncRequest extends AbstractRequest {

    private volatile CallBack callBack;

    private AsyncRequest() {
    }

    ;

    public AsyncRequest(ChannelHandlerContext ctx) {
        super(ctx);
    }

    public AsyncRequest callback(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public AsyncRequest raram(RpcVo req) {
        setReq(req);
        return this;
    }

    @Override
    public void finish() {
        if (callBack == null) {
            return;
        }
        RpcVo resp = getResp();
        if (resp.getCode() == 200) {
            callBack.success(resp);
        } else {
            callBack.exception(resp.getCode(), resp.getMsg());
        }
        callBack.completed();
    }

    public void excute() {
        RpcVo req = getReq();
        respMap.put(req.getMsgId(), this);
        getCtx().writeAndFlush(req);
    }

    public interface CallBack {
        void success(RpcVo result);

        void exception(Integer code, String cause);

        default void completed() {
        }

        ;

    }

}
