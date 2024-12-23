package com.openext.dev;

import com.openext.dev.impl.SimpleServletRequestImpl;
import com.openext.dev.impl.SimpleServletResponseImpl;
import com.openext.dev.interfaces.SimpleServlet;
import com.openext.dev.interfaces.SimpleServletRequest;
import com.openext.dev.interfaces.SimpleServletResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class CabinServletContainer {
    private Map<String, SimpleServlet> servlets = new HashMap<>();

    public void registerServlet(String path, SimpleServlet servlet) {
        servlets.put(path, servlet);
        servlet.init();
    }

    public void start(int port) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        }
    }

    private void handleRequest(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            SimpleServletRequest request = new SimpleServletRequestImpl(inputStream);
            SimpleServletResponse response = new SimpleServletResponseImpl(outputStream);

            SimpleServlet servlet = servlets.get(request.getPath());
            
            if (servlet != null) {
                servlet.service(request, response);
            } else {
                response.setStatus(404);
                response.writeBody("Not Found");
            }

            response.sendResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        CabinServletContainer container = new CabinServletContainer();

        // Register a simple servlet
        container.registerServlet("/hello", new SimpleServlet() {
            @Override
            public void init() {
                System.out.println("HelloServlet initialized");
            }

            @Override
            public void service(SimpleServletRequest request, SimpleServletResponse response) {
                response.writeBody("Hello, World!");
            }

            @Override
            public void destroy() {
                System.out.println("HelloServlet destroyed");
            }
        });

        container.start(8080);
    }
}
