package cn.kwafoo.handler;

import cn.kwafoo.constants.MsgType;
import cn.kwafoo.processor.ProcessorManager;
import cn.kwafoo.utils.IdUtil;
import cn.kwafoo.vo.RpcVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class ServerDispatcherHandler extends SimpleChannelInboundHandler<RpcVo> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcVo rpcVo) throws Exception {
        log.info("Inbound-收到消息 {}", rpcVo);
        // 业务分发
        Object resultBody = null;
        String msg = "ok";
        boolean exception = false;
        try {
            resultBody = ProcessorManager.process(rpcVo.getType(), rpcVo.getBody());
        } catch (Exception e) {
            log.error("Dispatcher handle err",e);
            msg = e.getMessage();
            exception = true;
        }

        // 返回结果
        RpcVo result = new RpcVo();
        result.setMsgId(IdUtil.uuid());
        result.setType(MsgType.getResp(rpcVo.getType()));
        result.setReqId(rpcVo.getMsgId());
        result.setCode(exception ? 500 : 200);
        result.setMsg(msg);
        result.setBody(resultBody);
        log.info("Inbound-回复消息 {}", result);
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive {}", ctx.channel());
//        super.channelActive(ctx);
        RpcVo rpcVo = new RpcVo();
        rpcVo.setType(MsgType.MSG_REQ.getCode());
        rpcVo.setMsgId(UUID.randomUUID().toString());
        rpcVo.setBody("服务器回应===>连接成功!");
        ctx.writeAndFlush(rpcVo);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive {}", ctx.channel());
        super.channelInactive(ctx);

    }
}
