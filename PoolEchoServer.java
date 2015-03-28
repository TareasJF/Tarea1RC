import java.net.*;
import java.io.*;


public class PoolEchoServer extends Thread {

  public final static int defaultPort = 2347;
  ServerSocket theServer;
  static int numberOfThreads = 10;
  
  
  public static void main(String[] args) {
  
    int port = defaultPort;
    
    try {
      port = Integer.parseInt(args[0]);
    }
    catch (Exception ex) {
    }
    if (port <= 0 || port >= 65536) port = defaultPort;
    
    try {
      ServerSocket ss = new ServerSocket(port);
      for (int i = 0; i < numberOfThreads; i++) {
        PoolEchoServer pes = new PoolEchoServer(ss); 
        pes.start();
      }
    }
    catch (IOException ex) {
      System.err.println(ex);
    }

  }
  
  public PoolEchoServer(ServerSocket ss) {
    theServer = ss;
  }
  
  public void run() {
  
    while (true) {
      try {
        Socket s = theServer.accept();
        OutputStream out = s.getOutputStream();
        InputStream in = s.getInputStream();
        while (true) {
          int n = in.read();
          if (n == -1) break;
          out.write(n);
          out.flush();
        } // end while
      } // end try
      catch (IOException ex) {
      }       
    } // end while
    
  } // end run
    
}