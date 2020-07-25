package com.opensource.printerservice.utils;

import com.opensource.printerservice.LoggerHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 09.07.2020 21:43
 */
public class Utilities {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static final String CHARSET_NAME = "ISO8859_9";
    private static final String IP_MASK = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    /**
     * Byte Array to String
     *
     * @param data byte array
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String binData2Str(final byte[] data) throws UnsupportedEncodingException {
        return new String(data, CHARSET_NAME).replace(String.valueOf(new char[1]), "");
    }

    public static boolean isNullOrEmpty(String string) {
        return !(string != null && string.length() != 0);
    }

    public static boolean setBaudrateSerialComm(String portPath, String baudRate) {
        String response = null;
        boolean result = true;
        try {
            Process p = null;
            String[] cmd = {"/bin/bash", "-c", "/bin/stty -F " + portPath + " " + baudRate};
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((response = br.readLine()) != null) {
                System.out.println(response);
            }
            while ((response = brError.readLine()) != null) {
                System.out.println(response);
                result = false;
            }
            p.destroy();
        } catch (Exception ex) {
            LoggerHelper.log(Level.SEVERE, ex);
        }
        return result;
    }

    public static String getAllIpAdresses() {
        String allIpAddresses = "";
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.<NetworkInterface>list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.<InetAddress>list(inetAddresses)) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().matches(IP_MASK)) {
                        allIpAddresses = String.valueOf(allIpAddresses) + " " + inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }

        return allIpAddresses;
    }
}
