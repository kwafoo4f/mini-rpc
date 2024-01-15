package cn.kwafoo.processor;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
public abstract class ProcessorAdaptor<R,P> implements Processor {

    /**
     * 类型强制转换
     *
     * @param param
     * @return
     */
    @Override
    public Object process(Object param) {
        return (R)(process0((P)param));
    }

    /**
     * 业务数据处理
     * @param param
     * @return
     */
    public abstract R process0(P param);
}
