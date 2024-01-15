package cn.kwafoo.vo;

import lombok.Data;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Data
public final class Response<T>{
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

}
