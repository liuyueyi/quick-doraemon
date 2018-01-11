package com.hust.hui.doraemon.core.util;

import org.junit.Test;

/**
 * Created by yihui on 2017/11/30.
 */
public class IpUtilsTest {

    @Test
    public void testGetLocalIp() {
        String ip = IpUtils.getLocalIP();
        System.out.println(ip);


        ip = IpUtils.getLocalIpByNetcard();
        System.out.println(ip);
    }
}
