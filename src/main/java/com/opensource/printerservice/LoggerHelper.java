package com.opensource.printerservice;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 08.07.2020 23:05
 */
public class LoggerHelper {

    private static LoggerHelper loggerHelper;
    private final Logger filelogger;
    private final Logger consolelogger;
    private static final int SIZE = 10;//Megabyte
    private static final int COUNT = 2;//Log File Count
    private static final boolean USECONSOLE = true;
    private static final boolean USEFILE = true;

    /**
     * private constructor for singleton
     */
    private LoggerHelper() {
        System.out.println("Logger");
        filelogger = Logger.getLogger("osfl");
        consolelogger = Logger.getLogger("oscl");
        FileHandler fh;
        try {
            int size = 1024 * 1024 * SIZE;
            fh = new FileHandler("%h/printerservice-%g.log", size, COUNT, true);
            fh.setLevel(Level.WARNING);//filelog level
            filelogger.addHandler(fh);
            filelogger.setUseParentHandlers(false);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.ALL);//consolelog level
            consolelogger.addHandler(ch);
            consolelogger.setUseParentHandlers(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static LoggerHelper getLoggerHelperInstance() {
        if (loggerHelper == null) {
            loggerHelper = new LoggerHelper();
        }
        return loggerHelper;
    }

    private static Logger getFileLoggerInstance() {
        return getLoggerHelperInstance().filelogger;
    }

    private static Logger getConsoleLoggerInstance() {
        return getLoggerHelperInstance().consolelogger;
    }

    public static void log(Level level, String message, Exception exception) {
        if (USEFILE) {
            getFileLoggerInstance().log(level, message, exception);
        }
        if (USECONSOLE) {
            getConsoleLoggerInstance().log(level, message, exception);
        }
    }

    public static void log(Level level, Exception exception) {
        log(level, null, exception);
    }

    public static void log(Level level, String message) {
        if (USEFILE) {
            getFileLoggerInstance().log(level, message);
        }
        if (USECONSOLE) {
            getConsoleLoggerInstance().log(level, message);
        }
    }
}
