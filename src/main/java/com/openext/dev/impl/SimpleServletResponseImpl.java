package com.openext.dev.impl;


import com.openext.dev.interfaces.SimpleServletResponse;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SimpleServletResponseImpl implements SimpleServletResponse {
    private OutputStream outputStream;
    private int statusCode = 200;
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();

    public SimpleServletResponseImpl(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void writeBody(String body) {
        this.body.append(body);
    }

    @Override
    public void sendResponse() {
        try {
            System.err.println("Sending response");

            // Map status codes to standard HTTP messages
            String statusMessage = switch (statusCode) {
                case 200 -> "OK";
                case 404 -> "Not Found";
                case 500 -> "Internal Server Error";
                default -> "Unknown";
            };

            // Build the response
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
            headers.forEach((key, value) -> response.append(key).append(": ").append(value).append("\r\n"));

            byte[] bodyBytes = body.toString().getBytes("UTF-8");
            response.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
            response.append("Connection: close\r\n");
            response.append("\r\n");

            // Write headers
            outputStream.write(response.toString().getBytes("UTF-8"));
            // Write body
            outputStream.write(bodyBytes);
            outputStream.flush();
        } catch (java.net.SocketException e) {
            System.err.println("Client disconnected before response was fully sent: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
