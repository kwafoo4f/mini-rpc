package cn.kwafoo.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
@Getter
@AllArgsConstructor
public enum MsgType {
    // 心跳
    HEARTBEAT_REQ((byte)1),
    HEARTBEAT_RESP((byte)2),

    // 业务
    MSG_REQ((byte)100),
    MSG_RESP((byte)101)
    ;

    private Byte code;

    public static MsgType getType(Byte code) {
        for (MsgType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    private static Map<Byte,Byte> respMap = new HashMap();
    static {
        // 心跳
        respMap.put(HEARTBEAT_REQ.code,HEARTBEAT_RESP.code);
        // 业务
        respMap.put(MSG_REQ.code,MSG_RESP.code);
    }

    public static Byte getResp(Byte reqCode) {
        return respMap.get(reqCode);
    }

}
