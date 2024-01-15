package cn.kwafoo.handler;

import cn.kwafoo.constants.MsgType;
import cn.kwafoo.node.NodeManager;
import cn.kwafoo.processor.ProcessorManager;
import cn.kwafoo.utils.IdUtil;
import cn.kwafoo.vo.RpcVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class HeartBeatHandler extends SimpleChannelInboundHandler<RpcVo> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcVo rpcVo) throws Exception {
        NodeManager.createOrRefresh(ctx.channel());
        if (!MsgType.HEARTBEAT_REQ.getCode().equals(rpcVo.getType())) {
            ctx.fireChannelRead(rpcVo);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("HeartBeatHandler-channelActive {}", ctx.channel());
        Channel channel = ctx.channel();
        NodeManager.createOrRefresh(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("HeartBeatHandler-channelInactive {}", ctx.channel());
        super.channelInactive(ctx);
    }
}
