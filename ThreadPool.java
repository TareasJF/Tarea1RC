import java.net.*;
import java.util.concurrent.Semaphore;

public class ThreadPool {
	Semaphore wLock = new Semaphore(1);
	Worker[] workers;
	
	public ThreadPool(int workersAmount){
		workers = new Worker[workersAmount];
		for(int i = 0; i < workersAmount; i++){
			workers[i] = new Worker();
		}
	}
	
	public void attend(Socket socket) throws Exception{
		wLock.acquire();
		Worker worker;
		while((worker = getWorker()) == null);
		worker.working = true;
		wLock.release();
		worker.execute(socket);
	}
	
	private Worker getWorker(){
		for (Worker worker : workers){
			if (worker.working == false)
				return worker;
		}
		return null;
	}
}