package cartago.infrastructure.android;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.ConditionVariable;
import android.os.IBinder;

/**
 * JaCaService connection used for binding at ICartagoNodeRemote
 *
 */
class JoinWspConnection implements ServiceConnection {

	private ICartagoNodeRemote mCartagoNodeRemote;
	private ConditionVariable mCondition;
	
	public JoinWspConnection() {
		mCondition = new ConditionVariable();
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mCartagoNodeRemote = ICartagoNodeRemote.Stub.asInterface(service);
		mCondition.open();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mCartagoNodeRemote = null;
	}
	
	/**
	 * Get CartagoNodeRemote
	 * @return CartagoNodeRemote
	 */
	public ICartagoNodeRemote getCartagoNodeRemote() {
		return mCartagoNodeRemote;
	}

	public void waitForServiceBinding() {
		mCondition.block();
	}	
}