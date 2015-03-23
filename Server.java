import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.util.Date;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.util.*;
import java.lang.*;

public class Server {
	public class HomeThread extends Thread{
		Boolean in_use;
		public void run(){
			this.in_use = true;
		}

	}

	public static void main(String[] args) throws Exception {

		System.out.print("Initializing server...\n");

		//Socket & port
		System.out.print("Binding port...\n");
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		System.out.print("Port binded...\n");

		//Threads
		System.out.print("Creating threads...\n");

		/*
		Threads[] pool = new Thread[20];
		for(int i = 0; i < pool.length; i++)
		{
			pool[i] = new Thread();
		}
		*/

		System.out.print("Threads created...\n");

		//Handlers
		server.createContext("/", new HomeHandler());
		server.createContext("/home_old", new HomeOldHandler());
		server.createContext("/secret", new SecretHandler());
		server.createContext("/login", new LoginHandler());

		//Starting
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.print("Server is up!");
		// String localIP = server.InetSocketAddress.getLocalHost().toString();
		// System.out.println(localIP);  
	}

	public static void log(String ip,String method, String address) throws IOException {
		Date date = new Date();
		String data = "["+ date.toString() + "]  " +ip + " " + method + " " + address + "\n";
		File file = new File("log.txt");
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fileWritter = new FileWriter(file.getName(),true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(data);
		bufferWritter.close();

		System.out.println(data);  
 
	}

	static class HomeHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			//We create a new thread here, temporarily
			Thread thrd = new Thread(new Runnable(){
	        	public void run(){
	        		try{
		        		System.out.print("Processing request\n");
						log(t.getRemoteAddress().toString(), "GET", "/");

						Headers h = t.getResponseHeaders();
						h.add("Content-Type","image/pdf");

						File file = new File ("Html/book.pdf");
						byte [] bytearray  = new byte [(int)file.length()];
						FileInputStream fis = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(fis);
						bis.read(bytearray, 0, bytearray.length);

						t.sendResponseHeaders(200, file.length());
						OutputStream os = t.getResponseBody();
						os.write(bytearray,0,bytearray.length);
						os.close();
						System.out.print("Request processed!\n");
					}
					catch(Exception e){

					}
	        	}
	        });
	        thrd.start();
		}
	}

	static class HomeOldHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			log(t.getRemoteAddress().toString(), "GET", "/home_old");

			Headers h = t.getResponseHeaders();
			h.add("Content-Type","text/html");

			File file = new File ("Html/home_old.html");
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			t.sendResponseHeaders(301, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
		}
	}

	static class SecretHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			// System.out.println(t.getRequestBody());  

			log(t.getRemoteAddress().toString(), "GET", "/secret");

			Headers h = t.getResponseHeaders();
			h.add("Content-Type","text/html");

			File file = new File ("Html/secret403.html");
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			t.sendResponseHeaders(403, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
		}
	}

	static class LoginHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			log(t.getRemoteAddress().toString(), "GET", "/login");

			Headers h = t.getResponseHeaders();
			h.add("Content-Type","text/html");

			File file = new File ("Html/login.html");
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
		}
	}
}