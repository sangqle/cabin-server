package com.openext.dev.interfaces;

public interface SimpleServletRequest {
    String getMethod();
    String getPath();
    String getHeader(String name);
    String getBody();
}