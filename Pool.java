import java.net.*;
import java.util.concurrent.Semaphore;

public class Pool {
	Worker[] workers;
	Semaphore requestWorker = new Semaphore(1);
	
	public Pool(int workersAmount){
		workers = new Worker[workersAmount];
		for(int i = 0; i < workersAmount; i++){
			workers[i] = new Worker(false);
		}
	}
	
	public void newConnection(Socket socket) throws Exception{
		/* Request one worker */
		requestWorker.acquire();
		Worker newWorker = findAvailableWorker();
		while(newWorker == null){
			newWorker = findAvailableWorker();
		}
		
		newWorker.working = true;
		requestWorker.release();
		
		/* Send task to worker */
		newWorker.execute(socket);
	}
	
	private Worker findAvailableWorker(){
		for (Worker worker : workers){
			if (!worker.working)
				return worker;
		}
		return null;
	}
	
}
