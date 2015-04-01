import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import com.sun.net.httpserver.*;

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
		server.createContext("/readme", new AboutHandler());

		//Starting
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.print("Server is up!\n");
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

  public static void cookie(String address) throws IOException {
    File file = new File("cookies.txt");
    
    if(!file.exists()) {
      file.createNewFile();
    }
    
    FileWriter fileWritter = new FileWriter(file.getName(),true);
    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    bufferWritter.write(address+"\n");
    bufferWritter.close();
  }

  public static Boolean checkCookies(String address) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("cookies.txt"));
    String line;
    Boolean logged = false;
    while((line = in.readLine()) != null)
    {
        if (line.equals(address)) {
          logged = true;
          break;
        }
    }
    in.close();
    return logged;
  }

	static class HomeHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			// //We create a new thread here, temporarily
			// Thread thrd = new Thread(new Runnable(){
	  //       	public void run(){
	  //       		try{
		 //        		System.out.print("Processing request\n");
						log(t.getRemoteAddress().toString(), "GET", "/");

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
						// System.out.print("Request processed!\n");
					// }
					// catch(Exception e){

					// }
	    //     	}
	    //     });
	    //     thrd.start();
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

      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();
   
      String line;
      try {
   
        br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
   
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
   
      String body = sb.toString();

      if (body.length() > 0) {
        log(t.getRemoteAddress().toString(), "POST", "/secret");
        String[] parts = body.split("&"); 
        
        System.out.println(parts[0] + " ||| " + parts[1]);

        if (parts[0].equals("login=root") && parts[1].equals("password=laboratorio1")) {
          cookie(t.getRemoteAddress().getAddress().toString());
          Headers h = t.getResponseHeaders();
          h.add("Content-Type","text/html");

          File file = new File ("Html/secret.html");
          byte [] bytearray  = new byte [(int)file.length()];
          FileInputStream fis = new FileInputStream(file);
          BufferedInputStream bis = new BufferedInputStream(fis);
          bis.read(bytearray, 0, bytearray.length);

          t.sendResponseHeaders(403, file.length());
          OutputStream os = t.getResponseBody();
          os.write(bytearray,0,bytearray.length);
          os.close();
          return;
        }
      } 
			log(t.getRemoteAddress().toString(), "GET", "/secret");

			Headers h = t.getResponseHeaders();
			h.add("Content-Type","text/html");
      if (checkCookies(t.getRemoteAddress().getAddress().toString())) {
        File file = new File ("Html/secret.html");
        byte [] bytearray  = new byte [(int)file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
        t.sendResponseHeaders(200, file.length());
        OutputStream os = t.getResponseBody();
        os.write(bytearray,0,bytearray.length);
        os.close();
      } else {
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

  static class AboutHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      log(t.getRemoteAddress().toString(), "GET", "/readme");

      Headers h = t.getResponseHeaders();
      h.add("Content-Type","text/md");

      File file = new File ("README.md");
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



// import java.io.*;
// import java.net.*;

// class Server
// {

// 	public static void main(String argv[]) throws Exception
// 	{
// 		@SuppressWarnings("resource")
// 		ServerSocket server_socket = new ServerSocket(8080);
// 		PrintWriter writer = new PrintWriter("log.txt");
// 		writer.close();
// 		Pool pool = new Pool(5);

// 		while(true)
// 		{
// 			Socket socket = server_socket.accept();
// 			if (socket != null){
// 				pool.newConnection(socket);
// 			}
// 	   }
//    }
// }