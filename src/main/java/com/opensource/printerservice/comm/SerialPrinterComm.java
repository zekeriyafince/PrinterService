package com.opensource.printerservice.comm;


import com.opensource.printerservice.LoggerHelper;
import com.opensource.printerservice.utils.Utilities;
import java.util.logging.Level;


/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 10.07.2020 08:35
 */
public class SerialPrinterComm implements IPrinterComm {

    JTermiosComm jTermiosComm = null;

    int serialFxTimeout = 25000;

    int selectTimeout = 1000;

    @Override
    public void sendData(byte[] data) {
        try {
            LoggerHelper.log(Level.SEVERE, "Send: " + Utilities.bytesToHex(data).toUpperCase());

            jTermiosComm.write(data);

            LoggerHelper.log(Level.SEVERE, "Send success");
        } catch (Exception e) {
            LoggerHelper.log(Level.SEVERE, "sendData: " + e.getMessage());
        }
    }

    @Override
    public byte readDataByte(int timeout) throws Exception {
        return new Byte("TO DO");
    }

    @Override
    public void readData(int timeout, byte[] data, int offset, int len) {
        try {
            
            int trLen = jTermiosComm.read(data, offset, len);
            if (trLen > 0) {
                System.out.println("" + new String(jTermiosComm.im_Buffer, offset, trLen));
            } else {
                LoggerHelper.log(Level.SEVERE, "No data received in " + String.valueOf(timeout));
            }

        } catch (Exception e) {
            LoggerHelper.log(Level.SEVERE, "readData: " + e.getMessage());
        }
    }

    @Override
    public void clearBuffer() throws Exception {
        try {
            int available = jTermiosComm.available();
            LoggerHelper.log(Level.SEVERE, "clear buffer");
            if (available > 0) {
                byte[] data = new byte[available];
                readData(100, data, 0, available);
            }
        } catch (Exception exception) {
            LoggerHelper.log(Level.SEVERE, "clear buffer error: " + exception.getMessage());
        }
        LoggerHelper.log(Level.SEVERE, "clear buffer exit");
    }

    @Override
    public void openCommunication() throws Exception {
        jTermiosComm = new JTermiosComm();

    }

}
