package it.unibo.pslab.jaca_android.core;

import java.util.concurrent.BlockingQueue;

import cartago.IBlockingCmd;

/**
 * Base class used for managing the dispatch of Android events
 * to CArtAgO Artifacts
 *    
 * @author asanti
 */
public class BaseEventFetcher implements IBlockingCmd {

	protected BlockingQueue<EventOpInfo> mEventsQueue;
	protected EventOpInfo mFetched;
	
	public BaseEventFetcher(){
		mEventsQueue = new java.util.concurrent.ArrayBlockingQueue<EventOpInfo>(100);
		mFetched = null;
	}
	
	/**
	 * Method that retrieves a new next event to be processed, blocking if necessary.
	 */
	@Override
	public void exec() {
		try {
			mFetched = mEventsQueue.take();
		} catch (Exception ex){
			mFetched = null;
		}
	}
	
	/**
	 * Retrieves the last fetched event
	 * @return
	 */
	public EventOpInfo getCurrentEventFetched(){
		return mFetched;
	}
	
	/**
	 * Puts a new event inside the event fetcher. This method is meant to
	 * be called by Android listeners (to GUI events, Sensors, etc.) for 
	 * adding a new Android event in the BaseEventFetcher.
	 *  
	 * @param ev
	 */
	public void putEvent(EventOpInfo ev){
		try {
			mEventsQueue.put(ev);
		} catch (Exception ignored){}
	}
}