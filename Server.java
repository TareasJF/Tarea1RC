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

public class Server {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/", new HomeHandler());
    server.createContext("/home_old", new HomeOldHandler());
    server.createContext("/secret", new SecretHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
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
 
  }

  static class HomeHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      log(t.getRemoteAddress().getAddress().toString(), "GET", "/");

      Headers h = t.getResponseHeaders();
      h.add("Content-Type","text/html");

      File file = new File ("Html/home.html");
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

  static class HomeOldHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      log(t.getRemoteAddress().getAddress().toString(), "GET", "/home_old");

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

      log(t.getRemoteAddress().getAddress().toString(), "GET", "/secret");

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
}