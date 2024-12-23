package com.openext.dev.interfaces;

public interface SimpleServlet {
    void init();
    void service(SimpleServletRequest request, SimpleServletResponse response);
    void destroy();
}



