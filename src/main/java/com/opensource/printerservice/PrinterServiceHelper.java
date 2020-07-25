package com.opensource.printerservice;

import com.opensource.printerservice.comm.PrinterComm;
import com.opensource.printerservice.model.PrinterResponse;
import com.opensource.printerservice.utils.PrinterConfiguration;
import com.opensource.printerservice.utils.Utilities;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 23.07.2020 12:07
 */
public class PrinterServiceHelper {

    static PrinterComm printerComm;

    public PrinterServiceHelper() throws Exception {
        printerComm = PrinterComm.getInstance();
        printerComm.startPosCom();
    }

    protected PrinterResponse sendData(String data) throws Exception {
        PrinterResponse pResponse = new PrinterResponse();
        printerComm.printerComm.sendData(data.getBytes(Utilities.CHARSET_NAME));
        pResponse.setSuccessful(true);
        return pResponse;
    }

    protected PrinterResponse readData(int length) throws Exception {
        PrinterResponse pResponse = new PrinterResponse();
        byte[] data = new byte[length];
        printerComm.printerComm.readData(PrinterConfiguration.MsgDurationTimeout, data, 0, length);
        pResponse.setSuccessful(true);
        pResponse.setReadData(Utilities.binData2Str(data));
        return pResponse;
    }

}
