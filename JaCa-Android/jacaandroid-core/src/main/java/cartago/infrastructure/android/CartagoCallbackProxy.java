package cartago.infrastructure.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import cartago.CartagoEvent;
import cartago.ICartagoCallback;

public class CartagoCallbackProxy implements ICartagoCallback, Parcelable {
	
	private ICartagoCallbackRemote mCallback;
	
	public CartagoCallbackProxy(ICartagoCallbackRemote callback){
		mCallback = callback;
	}
	
	public void notifyCartagoEvent(CartagoEvent ev){
		try {
			mCallback.notifyCartagoEvent(ev);
		} catch (RemoteException ignored){
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStrongBinder(mCallback.asBinder());
	}
	
	public void readFromParcel(Parcel in) {
		mCallback = ICartagoCallbackRemote.Stub.asInterface(in.readStrongBinder());
	}
	
	public static final Creator<CartagoCallbackProxy> CREATOR = new Creator<CartagoCallbackProxy>() {
        public CartagoCallbackProxy createFromParcel(Parcel in) {
            return new CartagoCallbackProxy(in);
        }

        public CartagoCallbackProxy[] newArray(int size) {
            return new CartagoCallbackProxy[size];
        }
    };

    private CartagoCallbackProxy(Parcel in) {
        readFromParcel(in);
    }

}
