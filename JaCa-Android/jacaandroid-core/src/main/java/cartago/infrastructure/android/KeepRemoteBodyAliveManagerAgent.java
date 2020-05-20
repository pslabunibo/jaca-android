package cartago.infrastructure.android;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import it.unibo.pslab.jaca_android.MasService;

public class KeepRemoteBodyAliveManagerAgent extends Thread {
	
	private ConcurrentLinkedQueue<AndroidAgentBodyProxyWrapper> proxies;
	private boolean stopped;
	private long delay;
	
	public KeepRemoteBodyAliveManagerAgent(ConcurrentLinkedQueue<AndroidAgentBodyProxyWrapper> proxies, long delay){
		setName("KeepRemoteBodyAliveManagerAgentAndroid");
		stopped = false;
		this.proxies = proxies;
		this.delay = delay;
	}
	
	public void run(){
		while (!stopped){
			try {
				sleep(delay);
				Iterator<AndroidAgentBodyProxyWrapper> it = proxies.iterator();
				while (it.hasNext()){
					AndroidAgentBodyProxyWrapper wrapper = it.next();
					try {
						wrapper.getProxy().ping();
					} catch (Exception ex){
						ex.printStackTrace();
						//Remote agent no more available, unbinding the service connection 
						MasService.getInstance().unbindService(wrapper.getConnection());
						it.remove();
					}
				}
			} catch (InterruptedException e) {
				
			} catch (Exception ex){
				log("Error: "+ex);
			}
		}
	}
	
	public void shutdown(){
		//Unbinding the connection with the JaCaServices
		Iterator<AndroidAgentBodyProxyWrapper> it = proxies.iterator();
		while (it.hasNext()){
			AndroidAgentBodyProxyWrapper wrapper = it.next();
			MasService.getInstance().unbindService(wrapper.getConnection());
		}
		interrupt();
		stopped = true;
	}
	
	private void log(String msg){
		System.err.println("[KEEP-ALIVE-MANAGER] "+msg);
	}
}
