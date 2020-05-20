package it.unibo.pslab.jaca_android.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.reflect.Method;

import cartago.CartagoException;
import cartago.IArtifactGuard;
import cartago.IArtifactOp;
import cartago.INTERNAL_OPERATION;
import cartago.OpFeedbackParam;

/**
 * Artifact that allow to interact with an existing Android Service that 
 * response at the specified action and map its interface 
 * into artifact operations. 
 * <br/><br/>
 * 
 * 
 * <p><strong> #### Observable Properties ##### </strong></p>
 * <p>
 *  <ul>
 *  	<li>
 *  	{@code state(State). State = {"connected", "disconnected"}}
 *  	</li>
 *  </ul> 
 * </p>
 *
 * <p><strong> #### Observable Events ##### </strong></p>
 * <p>
 *  <ul>
 *  	<li>
 *  	{@code service_connected}
 *  	</li>
 *  	<li>
 *  	{@code service_disconnected}
 *  	</li>
 *  </ul> 
 * </p>
 * 
 * @author mguidi
 *
 */
public class ServiceArtifact extends JaCaArtifact {
	
	/* event */
	public static final String SERVICE_CONNECTED = "service_connected";
	public static final String SERVICE_DISCONNETED = "service_disconnected";
	
	/**
	 * Observable property identifying the current state of the
	 * serive (i.e. if it is connected or not)
	 */
	public static final String STATE = "state";
	
	/**
	 * Value of the {@link ServiceArtifact#STATE} observable property
	 * indicating that the service has been connected to the artifact
	 * and its ready to be used
	 */
	public static final String CONNECTED = "connected"; // state value
	
	/**
	 * Value of the {@link ServiceArtifact#STATE} observable property
	 * indicating that the service has been disconnected to the artifact
	 */
	public static final String DISCONNECTED = "disconnected"; // state value
	
	private final String onServiceConnected = "onServiceConnected";
	private final String onServiceDisconnected = "onServiceDisconnected";
	
	private Intent mService;
	private ServiceArtifactConnection mConn;
	private BaseEventFetcher eventFetcher;
	private boolean mStopped;
	
	private Class<?> mServiceInterface;
	private Class<?> mServiceInterfaceStub;
	private Object mServiceInstance;
	
	/**
	 * Connect artifact to an Android Service
	 * @param action service action 
	 * @param interfaceName service interface name
	 * @throws CartagoException
	 */
	protected void init(String action, String interfaceName) throws CartagoException {
		try {
			// getCurrentTraumaLeader service interface class and service interface stub
			mStopped = false;
			mServiceInterface = Class.forName(interfaceName);
			Class<?>[] classes = mServiceInterface.getClasses();
			for(int i=0; i<classes.length; i++) {
				if(classes[i].getName().equals(interfaceName+"$Stub")) {
					mServiceInterfaceStub = classes[i];
					break;
				}
			}
			if (mServiceInterfaceStub == null) {
				throw new CartagoException("Stub class not found");
			}
		} catch (ClassNotFoundException e) {
			throw new CartagoException(e.getMessage());
		}
		
		Method[] methods = mServiceInterface.getMethods();
		IArtifactGuard guard = new TrueArtifactGuard();
		
		for (int i=0; i<methods.length; i++) {
			IArtifactOp op = new ServiceOpMethod(methods[i]);
			defineOp(op, guard);
		}
		
		eventFetcher = new BaseEventFetcher();
		defineObsProperty(STATE, DISCONNECTED);
		
		
		// bind to service
		mService = new Intent(action);
		mConn = new ServiceArtifactConnection();
		try {
			 if(!getApplicationContext().bindService(mService, mConn, Context.BIND_AUTO_CREATE)) {
				 throw new CartagoException("Unavailable service");
			 }
			 else{
				 execInternalOp("fetchConnectionEvents");
			 }
		} catch (SecurityException e) {
			throw new CartagoException(e.getMessage());
		}
	}
	
	protected void dispose() {
		mStopped = true;
		getApplicationContext().unbindService(mConn);
	}
	
	@INTERNAL_OPERATION void fetchConnectionEvents() throws CartagoException{
    	while (!mStopped){
    		await(eventFetcher);
    		EventOpInfo eventOp = eventFetcher.getCurrentEventFetched();
    		if (eventOp != null) {
	    		if (eventOp.getMethodName().equals(onServiceConnected)) {
	    			//ComponentName name = (ComponentName) eventOp.getParam()[0];
	    			IBinder service = (IBinder) eventOp.getParam()[1];
	    			
					try {
						Method asInterface = mServiceInterfaceStub.getMethod("asInterface", IBinder.class);
						mServiceInstance = asInterface.invoke(null, service);
						
					} catch (Exception e) {
						throw new CartagoException(e.getMessage());
					}
	    			
					signal(SERVICE_CONNECTED);
	    			getObsProperty(STATE).updateValue(CONNECTED);
	    			
	    		} else if(eventOp.getMethodName().equals(onServiceDisconnected)) {
	    			//ComponentName name = (ComponentName) eventOp.getParam()[0];
	    			mServiceInstance = null;
	    			signal(SERVICE_DISCONNETED);
	    			getObsProperty(STATE).updateValue(DISCONNECTED);
	    			
	    			// bind to service
	    			Intent intent = new Intent(mService);
	    			getApplicationContext().bindService(intent, mConn, Context.BIND_AUTO_CREATE);    			
	    		}
    		} else {
    			mStopped = true;
    		}
    	}
    }
	
	class ServiceArtifactConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			eventFetcher.putEvent(new EventOpInfo(ServiceArtifactConnection.this, onServiceConnected, name, service));
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			eventFetcher.putEvent(new EventOpInfo(ServiceArtifactConnection.this, onServiceDisconnected, name));
		}
	}
	
	class ServiceOpMethod implements IArtifactOp {
		private Method mMethod;		

		public ServiceOpMethod(Method method) {
			mMethod = method;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void exec(Object[] actualParams) throws Exception {
			try {
				Object[] param = new Object[(actualParams.length-1 <0) ? 0 : actualParams.length-1];

				for (int i = 0; i < param.length; i++) {
					param[i] = actualParams[i];
				}

				Object result = mMethod.invoke(mServiceInstance, param);
				if (actualParams.length>0 && actualParams[actualParams.length-1] instanceof OpFeedbackParam<?>) {
					OpFeedbackParam<Object> feedBack = (OpFeedbackParam<Object>) actualParams[actualParams.length-1];
					feedBack.set(result);
				}
			} catch(Exception e){
				failed(e.getLocalizedMessage());
			}
		}

		@Override
		public int getNumParameters(){
			if (mMethod.getReturnType() == Void.TYPE) {
				return mMethod.getParameterTypes().length;
			} else {
				return mMethod.getParameterTypes().length + 1;
			}
		}

		@Override
		public String getName(){
			return mMethod.getName();
		}
		
		@Override
		public boolean isVarArgs(){
			return mMethod.isVarArgs();
		}
		
	}

	class TrueArtifactGuard implements IArtifactGuard {

		@Override
		public boolean eval(Object[] actualParams) throws Exception {
			return true;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public int getNumParameters() {
			return 0;
		}
	}
}