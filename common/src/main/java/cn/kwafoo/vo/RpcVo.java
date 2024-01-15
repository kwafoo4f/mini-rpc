package cn.kwafoo.vo;

import lombok.Data;

/**
 * @description:
 * @author: kwafoo
 * @date: 2024-01-10
 */
@Data
public class RpcVo {
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 消息类型
     * @see cn.kwafoo.constants.MsgType
     */
    private Byte type;

    // 响应数据
    /**
     * 请求ID
     */
    private String reqId;
    /**
     * 响应编码
     */
    private Integer code;
    /**
     * 响应描述
     */
    private String msg;


    /**
     * 业务数据
     */
    Object body;

}
