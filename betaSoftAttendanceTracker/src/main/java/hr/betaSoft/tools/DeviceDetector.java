package hr.betaSoft.tools;

import jakarta.servlet.http.HttpServletRequest;

public class DeviceDetector {
    public static boolean isMobileDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && (userAgent.contains("Mobile") || userAgent.contains("Android") ||
        userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("Windows Phone"));
    }
}