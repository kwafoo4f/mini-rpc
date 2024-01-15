package cn.kwafoo.processor;

import cn.kwafoo.constants.MsgType;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
public class ClientInitial implements Initial{
    @Override
    public void initial() {

        // 业务处理注册
        ProcessorManager.put(MsgType.MSG_RESP.getCode(),new SayHiProcessor());
    }
}
