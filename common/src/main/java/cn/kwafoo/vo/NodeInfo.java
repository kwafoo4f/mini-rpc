package cn.kwafoo.vo;

import lombok.Data;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Data
public class NodeInfo {
    /**
     * 地址
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;

    /**
     * 最后一次心跳时间
     */
    private Long lastTime;

}
