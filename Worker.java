import java.io.*;
import java.util.*;
import java.net.*;


public class Worker implements Runnable{
	static boolean logged = false;
	static Semaphore writeToFile = new Semaphore(1), changeLogStatus = new Semaphore(1);
	
	boolean working;
	Socket socket;

	public Worker(boolean working) {
		super();
		this.working = working;
	}
	
	public void execute(Socket sckt){
		socket = sckt;
		this.run();
	}
	
	@Override
	public void run(){
		 try {
			DataOutputStream response = new DataOutputStream(socket.getOutputStream());
			BufferedReader client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	         
			String ipAddress = socket.getRemoteSocketAddress().toString();
			
			/* Get Stream Data */
			String inputLine;
			ArrayList<String> sArray = new ArrayList<String>();
			
			while ((inputLine = client.readLine()).length() != 0){
				if (inputLine.isEmpty())
					break;
			    sArray.add(inputLine.replace("\n", ""));
			}
			/* Convert to Hash */
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
			
			if (params.get("Request").equals("GET / HTTP/1.1")){
				response.writeBytes(
					"HTTP/1.1 200 OK\n"
					+"Content-Type: text/html\n"
					+"Connection: close\n"
					+"Server: DeathStar\n"
					+"\n"
					+"<html><h1>Heeeere's Johnny!</h1></html>\n"
				);
			}
			else if (params.get("Request").equals("GET /about HTTP/1.1")){
				response.writeBytes(
					"HTTP/1.1 200 OK\n"
					+"Content-Type: text/html\n"
					+"Connection: close\n"
					+"Server: DeathStar\n"
					+"\n"
					+"<html><h1>About the Death Star</h1></html>\n"
				);
			}
			else if (params.get("Request").equals("GET /old_home HTTP/1.1")){
				response.writeBytes(
					"HTTP/1.1 301 Moved Permanently\n"
					+"Content-Type: text/html\n"
					+"Connection: close\n"
					+"Server: DeathStar\n"
					+"\n"
					+"<html><h1>Heeeere's Johnny!</h1></html>\n"
				);
			}
			else if (params.get("Request").equals("GET /secret HTTP/1.1")){
				if (logged){
					response.writeBytes(
						"HTTP/1.1 403 Forbidden\n"
						+"Content-Type: text/html\n"
						+"Connection: close\n"
						+"Server: DeathStar\n"
						+"\n"
						+"<html><h1>May the force be with you</h1></html>\n"
					);
				}
				else{
					response.writeBytes(
						"HTTP/1.1 403 Forbidden\n"
						+"Content-Type: text/html\n"
						+"Connection: close\n"
						+"Server: DeathStar\n"
						+"\n"
						+"<html><h1>Pass you shall not!</h1></html>\n"
					);
				}
			}
			else if (params.get("Request").equals("POST /secret HTTP/1.1")){
		        String params = "";
		        while (client.ready()){
		        	params += (char) client.read();
		        }
				String[] var = params.replace("\n", "").split("&");
				String user = var[0].split("=")[1];
				String password = var[1].split("=")[1];

				if (user.equals("root") && password.equals("laboratorio1")){
					changeLogStatus.acquire();
					logged = true;
					changeLogStatus.release();
					response.writeBytes(
						"HTTP/1.1 200 OK\n"
						+"Content-Type: text/html\n"
						+"Connection: close\n"
						+"Server: DeathStar\n"
						+"\n"
						+"<html><h1>Force with you is</h1></html>\n"
					);
				}
				else{
					response.writeBytes(
						"HTTP/1.1 403 Forbidden\n"
						+"Content-Type: text/html\n"
						+"Connection: close\n"
						+"Server: DeathStar\n"
						+"\n"
						+"<html><h1>Pass you shall not!</h1></html>\n"
					);
				}
			}
			else if (params.get("Request").equals("GET /login HTTP/1.1")){
				response.writeBytes(
					"HTTP/1.1 200 Forbidden\n"
					+"Content-Type: text/html\n"
					+"Connection: close\n"
					+"Server: DeathStar\n"
					+"\n"
					+"<html><h1>Login you Must!</h1>"
					+ "<form action='/secret' method='post'>"
					+ "<div>"
					+ "<label for='user'>User</label>"
					+ "<input type='text' name='user' id='user' required='required'>"
					+ "</div>"
					+ "<div>"
					+ "<label for='password'>Pass</label>"
					+ "<input type='password' name='pass' id='pass' required='required'>"
					+ "</div>"
					+ "<input type='submit' value='Login'>"
					+ "</form>"
					+ "</html>\n"
				);
			}
			else{
				response.writeBytes(
					"HTTP/1.1 404 Not Found\n"
					+"Content-Type: text/html\n"
					+"Connection: close\n"
					+"Server: DeathStar\n"
					+"\n"
					+"<html><h1>These are not the droids you're looking for!</h1></html>\n"
				);
			}
			
			socket.close();
			socket = null;
			
			/* Write to log when connection success*/
			writeToFile.acquire();
			
			FileWriter writer = new FileWriter("log.txt", true);
			BufferedWriter out = new BufferedWriter(writer);
			out.write("\nIP: "+ipAddress+", Request: "+params.get("Request"));
			out.close();
			writer.close();
			
			writeToFile.release();
		} catch (Exception e) {
		}
		working = false;
	}

}
