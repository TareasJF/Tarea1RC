import java.io.*;
import java.net.*;

class Server
{

	public static void main(String argv[]) throws Exception
	{
		ServerSocket ss = new ServerSocket(8080);
		ThreadPool tp = new ThreadPool(10);

		while(true)
		{
			Socket socket = ss.accept();
			if (socket != null){
				tp.attend(socket);
			}
	   }
   }
}