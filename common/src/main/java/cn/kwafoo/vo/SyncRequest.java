package cn.kwafoo.vo;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @description: 同步请求
 * @author: zk
 * @date: 2024-01-10
 */
@Data
public final class SyncRequest extends AbstractRequest {

    private volatile CountDownLatch countDownLatch;

    private Long timeOut;

    private volatile Boolean finish;

    private SyncRequest() {
    }

    public SyncRequest(ChannelHandlerContext ctx, Long timeOut) {
        super(ctx);
        this.timeOut = timeOut;
        this.finish = false;
        countDownLatch = new CountDownLatch(1);
    }

    public SyncRequest raram(RpcVo req) {
        setReq(req);
        return this;
    }

    @Override
    public void finish() {
        this.finish = true;
        countDownLatch.countDown();
    }

    public RpcVo excute() throws TimeoutException {
        RpcVo req = getReq();
        respMap.put(req.getMsgId(), this);
        getCtx().writeAndFlush(req);
        try {
            countDownLatch.await(timeOut, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        AbstractRequest syncRequest = respMap.remove(req.getMsgId());
        if (!finish) {
            throw new TimeoutException("Timeout " + timeOut + "s");
        }
        return syncRequest.getResp();
    }

}
