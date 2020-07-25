package com.opensource.printerservice.model;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 22.07.2020 22:49
 */
public class PrinterResponse {

    private boolean successful;
    private int errorCode;
    private String readData;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReadData() {
        return readData;
    }

    public void setReadData(String readData) {
        this.readData = readData;
    }

}
