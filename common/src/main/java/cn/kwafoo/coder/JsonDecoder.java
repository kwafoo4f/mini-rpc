package cn.kwafoo.coder;

import cn.kwafoo.vo.RpcVo;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Slf4j
public class JsonDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        RpcVo rpcVo = JSON.parseObject(bytes, RpcVo.class);
        list.add(rpcVo);
    }
}
