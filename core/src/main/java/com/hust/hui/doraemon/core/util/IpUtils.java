package com.hust.hui.doraemon.core.util;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by yihui on 2017/11/30.
 */
public class IpUtils {
    /**
     * （1）先查找本机hostName；（2）根据hostName去dns查找对应的ip地址
     *
     * @return
     */
    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 直接根据第一个网卡地址作为其内网ipv4地址
     *
     * @return
     */
    public static String getLocalIpByNetcard() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp()) {
                        continue;
                    }
                    if (address.getAddress() instanceof Inet4Address) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
                        return inet4Address.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将形如“10.11.3.30:20019”的地址解析为InetSocketAddress
     *
     * @param address
     * @return
     */
    public static InetSocketAddress parseSocketAddress(String address) {
        String seperator = ":";
        String[] parts = address.split(seperator, 2);
        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
    }


}
