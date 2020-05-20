package cartago.infrastructure.android;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

class GarbageBodyCollectorAgent extends Thread {

	private ConcurrentLinkedQueue<AgentBodyRemote> remoteCtxs;
	private boolean stopped;
	private long delay;
	private long timeout;
	
	public GarbageBodyCollectorAgent(ConcurrentLinkedQueue<AgentBodyRemote> contexts, long delay, long timeout){
		setName("GarbageBodyCollectorAgentAndroid");
		this.delay = delay;
		this.remoteCtxs = contexts;
		this.timeout = timeout;
	}
	
	public synchronized void stopActivity(){
		stopped = true;
		this.interrupt();
	}

	public synchronized boolean isStopped(){
		return stopped;
	}
			
	public void run(){
		stopped = false;
		while (!isStopped()){
			try {
				sleep(delay);
				Iterator<AgentBodyRemote> it = remoteCtxs.iterator();
				while (it.hasNext()){
					AgentBodyRemote ctx = it.next();
					try {
						long last = ctx.getLastPing();
						if (System.currentTimeMillis()-last > timeout){
							ctx.getContext().getWSPKernel().removeGarbageBody(ctx.getContext());
							it.remove();
							log("body garbaged: "+ctx.getContext().getAgentId());
						}
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}
			} catch (Throwable ignored){
			}
		}
	}

	private void log(String msg){
		System.out.println("[GarbageBody Collector] "+msg);
	}
}
