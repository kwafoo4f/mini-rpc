package cn.kwafoo.utils;

import java.util.UUID;

/**
 * @description:
 * @author: zk
 * @date: 2024-01-10
 */
public final class IdUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","");
    }

}
