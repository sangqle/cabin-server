package com.openext.dev.interfaces;

public interface SimpleServletResponse {
    void setStatus(int statusCode);
    void setHeader(String name, String value);
    void writeBody(String body);
    void sendResponse() throws Exception;
}
