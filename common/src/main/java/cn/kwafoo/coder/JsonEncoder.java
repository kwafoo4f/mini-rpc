package cn.kwafoo.coder;

import cn.kwafoo.vo.RpcVo;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class JsonEncoder extends MessageToByteEncoder<RpcVo> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcVo baseVo, ByteBuf out) throws Exception {
        if (baseVo != null) {
            out.writeBytes(JSON.toJSONBytes(baseVo));
        }
    }
}
