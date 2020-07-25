package com.opensource.printerservice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opensource.printerservice.model.PrinterResponse;
import com.opensource.printerservice.utils.Utilities;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 *
 * @author Zekeriya Furkan İNCE
 * @date 12.07.2020 01:21
 */
public class MainApp {

    static Gson gson;
    static PrinterServiceHelper printerServiceHelper;
    private static final int port = 9100;

    public static void main(String[] args) {
        LoggerHelper.log(Level.INFO, "HTTP Printer Service starting..");
        LoggerHelper.log(Level.INFO, "IP Adresses List :  " + Utilities.getAllIpAdresses() + " Port : " + port + " listens for incoming TCP connections from clients on this address.");
        gson = new Gson();

        try {

            printerServiceHelper = new PrinterServiceHelper();

            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            httpServer.createContext("/sendData", new HttpHandler() {
                @Override
                public void handle(HttpExchange he) throws IOException {
                    LoggerHelper.log(Level.INFO, "request: /sendData");
                    PrinterResponse printerResponse = null;

                    try {
                        String readBody = readBody(he);
                        JsonObject jsonObject = gson.fromJson(readBody, JsonObject.class);
                        String data = gson.fromJson(jsonObject.get("data"), String.class);
                        printerResponse = printerServiceHelper.sendData(data);
                    } catch (Exception e) {
                        printerResponse = new PrinterResponse();
                        printerResponse.setErrorCode(101);
                        LoggerHelper.log(Level.SEVERE, e);
                    } finally {
                        String response = gson.toJson(printerResponse);
                        writeResponse(he, response);
                        LoggerHelper.log(Level.INFO, "response: " + response);
                    }

                }
            });

            httpServer.createContext("/readData", new HttpHandler() {
                @Override
                public void handle(HttpExchange he) throws IOException {
                    LoggerHelper.log(Level.INFO, "request: /readData");
                    PrinterResponse printerResponse = null;

                    try {
                        String readBody = readBody(he);
                        JsonObject jsonObject = gson.fromJson(readBody, JsonObject.class);
                        int length = gson.fromJson(jsonObject.get("length"), Integer.class);
                        printerResponse = printerServiceHelper.readData(length);
                    } catch (Exception e) {
                        printerResponse = new PrinterResponse();
                        printerResponse.setErrorCode(101);
                        LoggerHelper.log(Level.SEVERE, e);
                    } finally {
                        String response = gson.toJson(printerResponse);
                        writeResponse(he, response);
                        LoggerHelper.log(Level.INFO, "response: " + response);
                    }

                }
            });

            httpServer.start();

        } catch (Exception ex) {
            LoggerHelper.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public static void writeResponse(HttpExchange he, String response) {
        try {
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
            System.out.println("responseBody:" + response);
        } catch (IOException ex) {
            LoggerHelper.log(Level.SEVERE, null, ex);
        }
    }

    private static String readBody(HttpExchange he) throws IOException {
        InputStreamReader isr = null;
        String request = null;
        try {
            isr = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            request = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            LoggerHelper.log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                LoggerHelper.log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("requestBody:" + request + "\n");
        return request;
    }
}
