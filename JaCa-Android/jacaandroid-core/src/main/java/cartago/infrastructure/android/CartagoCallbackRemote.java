package cartago.infrastructure.android;

import android.os.RemoteException;

import cartago.CartagoEvent;
import cartago.ICartagoCallback;

public class CartagoCallbackRemote extends ICartagoCallbackRemote.Stub {

	private ICartagoCallback mCallback;
	
	public CartagoCallbackRemote(ICartagoCallback callback) {
		mCallback = callback;
	}
	
	@Override
	public void notifyCartagoEvent(CartagoEvent ev) throws RemoteException {
		mCallback.notifyCartagoEvent(ev);
	}
}