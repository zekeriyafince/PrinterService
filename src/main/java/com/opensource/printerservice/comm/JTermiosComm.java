package com.opensource.printerservice.comm;

import com.opensource.printerservice.utils.PrinterConfiguration;
import java.io.IOException;
import jtermios.Termios;
import purejavacomm.UnsupportedCommOperationException;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 08.07.2020 18:52
 */
public class JTermiosComm {

    private int fileHandle;
    private boolean isOpen;

    public byte[] im_Buffer = new byte[2048];

    public JTermiosComm() throws IOException, UnsupportedCommOperationException {
        open(PrinterConfiguration.ComPort);
        configureSerialPort(PrinterConfiguration.BaudRate, PrinterConfiguration.DataBits, PrinterConfiguration.StopBits, PrinterConfiguration.Parity);
        jtermios.JTermios.fcntl(fileHandle, TermiosConstants.MY_F_SETFL, 0);
        isOpen = true;
    }

    private void open(String portName) throws IOException {
        fileHandle = -1;
        int mode = TermiosConstants.MY_O_RDWR | TermiosConstants.MY_O_NOCTTY | TermiosConstants.MY_O_NONBLOCK;
        fileHandle = jtermios.JTermios.open(portName, mode);
        if (fileHandle == -1) {
            throw new IOException("Open of \"" + portName + "\" failed.");
        }
    }

    private void configureSerialPort(int baudRate, int dataBits, int stopBits, int parity) throws IOException, UnsupportedCommOperationException {
        Termios cfg = new Termios();

        jtermios.JTermios.tcgetattr(fileHandle, cfg);
        cfg.c_lflag &= ~(TermiosConstants.MY_ICANON | TermiosConstants.MY_ECHO | TermiosConstants.MY_ECHOE | TermiosConstants.MY_ISIG);
        cfg.c_cflag &= ~TermiosConstants.MY_PARENB;
        switch (parity) {
            case 0:     //SerialPort.PARITY_NONE:
                break;
            case 2:     //SerialPort.PARITY_EVEN:
                cfg.c_cflag |= TermiosConstants.MY_PARENB;
                break;
            case 1:     //SerialPort.PARITY_ODD:
                cfg.c_cflag |= TermiosConstants.MY_PARENB;
                cfg.c_cflag |= TermiosConstants.MY_PARODD;
                break;
            case 3:     //SerialPort.PARITY_MARK:
                cfg.c_cflag |= TermiosConstants.MY_PARENB;
                cfg.c_cflag |= TermiosConstants.MY_CMSPAR;
                cfg.c_cflag |= TermiosConstants.MY_PARODD;
                break;
            case 4:     //SerialPort.PARITY_SPACE:
                cfg.c_cflag |= TermiosConstants.MY_PARENB;
                cfg.c_cflag |= TermiosConstants.MY_CMSPAR;
                break;
            default:
                throw new UnsupportedCommOperationException("parity = " + parity);
        }
        if (encodeStopBit(stopBits) == 2) {
            cfg.c_cflag |= TermiosConstants.MY_CSTOPB;
        } else {
            cfg.c_cflag &= ~TermiosConstants.MY_CSTOPB;
        }
        //cfg.c_cflag |= TermiosConstants.MY_CSTOPB; 
        cfg.c_cflag &= ~TermiosConstants.MY_CSIZE;
        cfg.c_cflag |= encodeDataBit(dataBits);
        cfg.c_oflag &= ~TermiosConstants.MY_OPOST;
        cfg.c_iflag &= ~TermiosConstants.MY_INPCK;
        cfg.c_iflag &= ~(TermiosConstants.MY_IXON | TermiosConstants.MY_IXOFF | TermiosConstants.MY_IXANY);
        cfg.c_cc[TermiosConstants.MY_VMIN] = 0;
        cfg.c_cc[TermiosConstants.MY_VTIME] = 2;

        int baudRateCode = encodeBaudRate(baudRate);
        jtermios.JTermios.cfsetospeed(cfg, baudRateCode);//sets the output baud rate
        jtermios.JTermios.cfsetispeed(cfg, baudRateCode); //sets the input baud rate

        jtermios.JTermios.tcsetattr(fileHandle, TermiosConstants.MY_TCSANOW, cfg);

        jtermios.JTermios.tcflush(fileHandle, TermiosConstants.MY_TCIOFLUSH);
    }

    private static int encodeBaudRate(int baudRate) {
        switch (baudRate) {
            case 9600:
                return TermiosConstants.MY_B9600;
            case 115200:
                return TermiosConstants.MY_B115200;
            default:
                throw new IllegalArgumentException("Unsupported baud rate " + baudRate + ".");
        }
    }

    private static int encodeDataBit(int dataBits) throws UnsupportedCommOperationException {
        int db;
        switch (dataBits) {
            case 5:     //SerialPort.DATABITS_5
                db = TermiosConstants.MY_CS5; //CS5
                break;
            case 6:      //SerialPort.DATABITS_6
                db = TermiosConstants.MY_CS6; //CS6
                break;
            case 7:      //SerialPort.DATABITS_7
                db = TermiosConstants.MY_CS7; //CS7
                break;
            case 8:      //SerialPort.DATABITS_8
                db = TermiosConstants.MY_CS8; //CS8 - 8 data bit format
                break;
            default:
                throw new UnsupportedCommOperationException("dataBits = " + dataBits);
        }
        return db;
    }

    private static int encodeStopBit(int stopBits) throws UnsupportedCommOperationException {
        int sb;
        switch (stopBits) {
            case 1:     //SerialPort.STOPBITS_1
                sb = 1;
                break;
            case 3:     //SerialPort.STOPBITS_1_5
                sb = 2;
                break;
            case 2:     //SerialPort.STOPBITS_2
                sb = 2;
                break;
            default:
                throw new UnsupportedCommOperationException("stopBits = " + stopBits);
        }
        return sb;
    }

    // Writes to the channel without blocking.
    // Returns the number of bytes actually written.
    private void write(byte[] buffer, int offset, int length) throws IOException {

        if (buffer == null) {
            throw new IllegalArgumentException();
        }
        if (offset < 0 || length < 0 || offset + length > buffer.length) {
            throw new IndexOutOfBoundsException("buffer.lengt " + buffer.length + " offset " + offset + " length " + length);
        }
        while (length > 0) {
            int n = buffer.length - offset;
            if (n > im_Buffer.length) {
                n = im_Buffer.length;
            }
            if (n > length) {
                n = length;
            }
            if (offset > 0) {
                System.arraycopy(buffer, offset, im_Buffer, 0, n);
                n = jtermios.JTermios.write(fileHandle, im_Buffer, n);
            } else {
                n = jtermios.JTermios.write(fileHandle, buffer, n);
            }

            if (n < 0) {
                System.out.println("write() failed error no: " + jtermios.JTermios.errno());
                close();
                throw new IOException();
            }

            length -= n;
            offset += n;
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {

        if (buffer == null) {
            throw new IllegalArgumentException();
        }
        if (length == 0) {
            return 0;
        }
        if (offset < 0 || length < 0 || offset + length > buffer.length) {
            throw new IndexOutOfBoundsException("buffer.lengt " + buffer.length + " offset " + offset + " length " + length);
        }

        int reqLen = Math.min(im_Buffer.length, length);
        if (reqLen <= 0) {
            return 0;
        }
        int trLen = jtermios.JTermios.read(fileHandle, buffer, reqLen);
        if (trLen == -1) {
            System.out.println("Serial port read error failed error no: " + jtermios.JTermios.errno());
            close();
            throw new IOException();
        }
        if (trLen < 0 || trLen > reqLen) {
            throw new RuntimeException("Invalid length returned from read().");
        }
        getBytes(buffer, offset, trLen, im_Buffer, offset);
        return trLen;
    }

    public int available() throws IOException {
        int[] im_Available = {0};
        if (fileHandle < 0) {
            return 0;
        }

        if (jtermios.JTermios.ioctl(fileHandle, TermiosConstants.MY_FIONREAD, im_Available) < 0) {
            close();
            System.out.println("available errno: " + jtermios.JTermios.errno());
            throw new IOException();
        }
        return im_Available[0];
    }

    public static void getBytes(byte[] source, int srcBegin, int srcEnd, byte[] destination, int dstBegin) {
        System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd - srcBegin);
    }

    public void close() throws IOException {
        if (!isOpen) {
            return;
        }
        isOpen = false;
        close2();
    }

    private void close2() throws IOException {
        if (fileHandle != -1) {
            jtermios.JTermios.close(fileHandle);
        }
    }

}
