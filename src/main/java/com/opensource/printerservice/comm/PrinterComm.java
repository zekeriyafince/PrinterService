package com.opensource.printerservice.comm;

import com.opensource.printerservice.LoggerHelper;
import com.opensource.printerservice.utils.PrinterConfiguration;
import com.opensource.printerservice.utils.Utilities;
import java.util.logging.Level;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 10.07.2020 12:11
 */
public class PrinterComm {

    public IPrinterComm printerComm;

    private static PrinterComm posComInstance;

    public static PrinterComm getInstance() {
        if (posComInstance == null)
      try {
            posComInstance = new PrinterComm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posComInstance;
    }

    public void startPosCom() throws Exception {
        if (Utilities.isNullOrEmpty(PrinterConfiguration.ComPort)) {
            this.printerComm = new SerialPrinterComm();
        } else {
            throw new Exception("Unexpected CommPort path" + PrinterConfiguration.ComPort);
        }
        try {
            this.printerComm.openCommunication();
        } catch (Exception e) {
            LoggerHelper.log(Level.SEVERE, "not starting communication serial port :" + PrinterConfiguration.ComPort);
            throw e;
        }
    }
}
