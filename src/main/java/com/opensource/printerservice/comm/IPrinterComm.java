package com.opensource.printerservice.comm;

/**
 * Printer communication service interface
 * 
 * @author Zekeriya Furkan İNCE
 * @date 09.07.2020 14:02
 */
public interface IPrinterComm {

    void sendData(byte[] paramArrayOfbyteData) throws Exception;

    byte readDataByte(int paramIntTimeout) throws Exception;

    void readData(int paramIntTimeout, byte[] paramArrayOfbyteData, int paramIntOffset, int paramIntLen) throws Exception;

    void clearBuffer() throws Exception;

    void openCommunication() throws Exception;
}
