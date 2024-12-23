package com.openext.dev.impl;

import com.openext.dev.interfaces.SimpleServletRequest;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleServletRequestImpl implements SimpleServletRequest {
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public SimpleServletRequestImpl(InputStream inputStream) {
        parseRequest(inputStream);
    }

    private void parseRequest(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read the request line
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                throw new IOException("Malformed HTTP request: Request line is empty");
            }

            // Split the request line into components
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length != 3) {
                throw new IOException("Malformed HTTP request: Invalid request line");
            }

            // Extract the HTTP method, path, and version
            method = requestParts[0];
            path = requestParts[1];
            httpVersion = requestParts[2];

            // Parse headers
            headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            // If the request is POST or PUT, read the body
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                String contentLength = headers.get("Content-Length");
                if (contentLength != null) {
                    int length = Integer.parseInt(contentLength);
                    char[] bodyChars = new char[length];
                    if (reader.read(bodyChars) < length) {
                        throw new IOException("Malformed HTTP request: Body size mismatch");
                    }
                    body = new String(bodyChars);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public String getBody() {
        return body;
    }
}
