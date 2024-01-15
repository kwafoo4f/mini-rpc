package cn.kwafoo.processor;

import cn.kwafoo.constants.MsgType;
import cn.kwafoo.node.NodeManager;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
public class ServerInitial implements Initial{
    @Override
    public void initial() {
        // 启动心跳检查任务
        NodeManager.init();

        // 业务处理注册
        ProcessorManager.put(MsgType.MSG_REQ.getCode(),new RepeatProcessor());
    }
}
