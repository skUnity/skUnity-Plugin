package com.skunity.plugin.connect;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.skunity.plugin.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebConnector {

	public WebConnector() {
	    HttpServer server = null;
	    InetSocketAddress address = new InetSocketAddress(0);
		try {
			server = HttpServer.create(address, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    server.createContext("/test", new MyHandler());
	    server.setExecutor(null);
	    server.start();
	    Logger.info(server.getAddress().getHostName());
	    Logger.info(server.getAddress().getHostString());
	    Logger.info("" + server.getAddress().getPort());
	}

	static class MyHandler implements HttpHandler {
	    @Override
	    public void handle(HttpExchange t) throws IOException {
	        String response = "This is the response";
	        t.sendResponseHeaders(200, response.length());
	        OutputStream os = t.getResponseBody();
	        os.write(response.getBytes());
	        os.close();
	    }
	}

}
