package cn.kwafoo.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 业务处理器管理
 * @author: kwafoo
 * @date: 2024-01-10
 */
public class ProcessorManager {
    /**
     * Key:MsgType,Value:业务处理器
     */
    private static final Map<Byte,Processor> processorMap = new HashMap<>();

    /**
     * 添加业务处理器
     * @param type
     * @param processor
     */
    public static void put(Byte type,Processor processor) {
        processorMap.put(type,processor);
    }

    /**
     * 移除业务处理器
     * @param type
     */
    public static void remove(Byte type) {
        if (type == null) {
            return;
        }
        if (processorMap.containsKey(type)) {
            processorMap.remove(type);
        }
    }

    /**
     * 业务分发
     *
     * @param type
     * @param param
     * @return
     */
    public static Object process(Byte type,Object param) {
        Processor processor = processorMap.get(type);
        if (processor == null) {
            throw new UnsupportedOperationException("没有这样的业务处理器");
        }
        return processor.process(param);
    }

}
