package it.unibo.pslab.jaca_android.core;

/**
 * Class representing a generic Android Event (GUI events, Sensors events, etc).
 * @author asanti
 */
public class EventOpInfo {

	private String mMethodName;
	private Object mSource;
	private Object[] mParam;
	
	/**
	 * Construct a new EventOpInfo identifying the occurred event with the parameters provided
	 * in input
	 * @param source The object that generates the event 
	 * @param methodName The Android method that was responsible of the event handling (i.e.
	 * a listener) 
	 * @param param The params of the method that was responsible of the event handling
	 */
	public EventOpInfo(Object source, String methodName, Object... param){
		mSource = source;
		mMethodName = methodName;
		mParam = param;
	}
	
	/**
	 * Returns the name of the Android method that was responsible of the event handling
	 * @return
	 */
	public String getMethodName() {
		return mMethodName;
	}
	
	/**
	 * Returns the object that generates the event 
	 * @return
	 */
	public Object getSource(){
		return mSource;
	}
	
	/**
	 * Returns the params of the method that was responsible of the event handling
	 * @return
	 */
	public Object[] getParam() {
		return mParam;
	}
}