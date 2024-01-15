package cn.kwafoo.processor;

/**
 * @description: 原始消息返回
 * @author: zk
 * @date: 2024-01-10
 */
public class RepeatProcessor extends ProcessorAdaptor<Object, Object> {

    @Override
    public Object process0(Object param) {
        return param;
    }
}
