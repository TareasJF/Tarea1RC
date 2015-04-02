import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.Semaphore;


public class Worker implements Runnable{
	boolean working;
	Socket socket;
	Semaphore logLock = new Semaphore(1);
	Semaphore cookieLock = new Semaphore(1);
	
	public Worker() {
		super();
		this.working = false;
	}
	
	public void execute(Socket sckt){
		socket = sckt;
		this.run();
	}

	public void log(String ip,String uri) throws Exception {
		Date date = new Date();
		String data = "["+ date.toString() + "]  " +ip + " " + uri + "\n";

		logLock.acquire();
		
		File file = new File("log.txt");
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fileWritter = new FileWriter(file.getName(),true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(data);
		bufferWritter.close();

		System.out.println(data);  

 		logLock.release();
	}

  public void cookie(String address) throws Exception {
  	cookieLock.acquire();

    File file = new File("cookies.txt");
    if(!file.exists()) {
      file.createNewFile();
    }
    
    FileWriter fileWritter = new FileWriter(file.getName(),true);
    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    bufferWritter.write(address+"\n");
    bufferWritter.close();

    cookieLock.release();
  }

  public Boolean checkCookies(String address) throws Exception {
  	cookieLock.acquire();
    
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

    cookieLock.release();
    return logged;
  }
	
	@Override
	public void run(){
		 try {
			DataOutputStream response = new DataOutputStream(socket.getOutputStream());
			BufferedReader client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	         
			String ipAddress = socket.getInetAddress().toString();

			String inputLine;
			ArrayList<String> sArray = new ArrayList<String>();
			
			while ((inputLine = client.readLine()).length() != 0){
				if (inputLine.isEmpty())
					break;
			    sArray.add(inputLine.replace("\n", ""));
			}

			Hashtable<String,String> params = new Hashtable<String,String>();
			
			for (int i=0; i<sArray.size(); i++){
				if (i == 0){
					params.put("Request", sArray.get(i));
				}
				else{
					String key = sArray.get(i).split(":")[0];
					String val = sArray.get(i).split(":")[1];
					params.put(key,val);
				}
			}

			String uri = params.get("Request").split("HTTP")[0];

			if (uri.equals("GET / ")) {
				File myFile = new File ("Html/home.html");
	      byte [] mybytearray  = new byte [(int)myFile.length()];
	      FileInputStream fis = new FileInputStream(myFile);
	      BufferedInputStream bis = new BufferedInputStream(fis);
	      bis.read(mybytearray,0,mybytearray.length);
	      response.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/html\nConnection: close\n");
	      response.write(mybytearray,0,mybytearray.length);
	      response.flush();
	      log(ipAddress, uri);
				
			}
			else if (uri.equals("GET /home_old ")) {
				File myFile = new File ("Html/home_old.html");
	      byte [] mybytearray  = new byte [(int)myFile.length()];
	      FileInputStream fis = new FileInputStream(myFile);
	      BufferedInputStream bis = new BufferedInputStream(fis);
	      bis.read(mybytearray,0,mybytearray.length);
	      response.writeBytes("HTTP/1.1 301 Moved Permanently\nContent-Type: text/html\nConnection: close\n");
	      response.write(mybytearray,0,mybytearray.length);
	      response.flush();
	      log(ipAddress, uri);
			}
			else if (uri.equals("GET /login ")) {
				File myFile = new File ("Html/login.html");
	      byte [] mybytearray  = new byte [(int)myFile.length()];
	      FileInputStream fis = new FileInputStream(myFile);
	      BufferedInputStream bis = new BufferedInputStream(fis);
	      bis.read(mybytearray,0,mybytearray.length);
	      response.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/html\nConnection: close\n");
	      response.write(mybytearray,0,mybytearray.length);
	      response.flush();
	      log(ipAddress, uri);
			}
			else if (uri.equals("GET /secret ")) {
				if (checkCookies(ipAddress)) {
					File myFile = new File ("Html/secret.html");
		      byte [] mybytearray  = new byte [(int)myFile.length()];
		      FileInputStream fis = new FileInputStream(myFile);
		      BufferedInputStream bis = new BufferedInputStream(fis);
		      bis.read(mybytearray,0,mybytearray.length);
		      response.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/html\nConnection: close\n");
		      response.write(mybytearray,0,mybytearray.length);
		      response.flush();
				}
				else {
					File myFile = new File ("Html/secret403.html");
		      byte [] mybytearray  = new byte [(int)myFile.length()];
		      FileInputStream fis = new FileInputStream(myFile);
		      BufferedInputStream bis = new BufferedInputStream(fis);
		      bis.read(mybytearray,0,mybytearray.length);
		      response.writeBytes("HTTP/1.1 403 Forbidden\nContent-Type: text/html\nConnection: close\n");
		      response.write(mybytearray,0,mybytearray.length);
		      response.flush();
				}
	      log(ipAddress, uri);
			}
			else if (uri.equals("POST /secret ")) {
				String body = "";
        while (client.ready()){
        	body += (char) client.read();
        }
				body = body.replace("\n", "");

				if (body.equals("login=root&password=laboratorio1&commit=Login")){
					cookie(ipAddress);
				}
	      response.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/html\nConnection: close\n\n<!DOCTYPE html><html><head><meta http-equiv='refresh' content='0;url=/secret'/></head></html>");
	      response.flush();
	      log(ipAddress, uri);
			}
			else if (uri.equals("GET /readme ")) {
				File myFile = new File ("README.md");
	      byte [] mybytearray  = new byte [(int)myFile.length()];
	      FileInputStream fis = new FileInputStream(myFile);
	      BufferedInputStream bis = new BufferedInputStream(fis);
	      bis.read(mybytearray,0,mybytearray.length);
	      response.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/txt\nConnection: close\n");
	      response.write(mybytearray,0,mybytearray.length);
	      response.flush();
	      log(ipAddress, uri);
			}
			else {
	      response.writeBytes("HTTP/1.1 404 Not Found\nContent-Type: text/html\nConnection: close\n\n<!DOCTYPE html><html><head><meta http-equiv='refresh' content='3;url=/'/></head>404 NOT FOUND</html>");
	      response.flush();
	      log(ipAddress, uri);

			}
			socket.close();
			socket = null;

		} catch (Exception e) {
		}
		working = false;
	}

}
