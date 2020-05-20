package cartago.infrastructure.android;


import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import cartago.CartagoEvent;

public interface ICartagoCallbackRemote extends IInterface {

	void notifyCartagoEvent(CartagoEvent ev) throws RemoteException;
	
	/** Local-side IPC implementation stub class. */
	abstract class Stub extends android.os.Binder implements ICartagoCallbackRemote {
		private static final String DESCRIPTOR = "cartago.infrastructure.android.ICartagoCallbackRemote";
		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}
		/**
		 * Cast an IBinder object into an ICartagoCallbackRemote interface,
		 * generating a proxy if needed.
		 */
		public static ICartagoCallbackRemote asInterface(IBinder obj) {
			if ((obj==null)) {
				return null;
			}
			IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin!=null)&&(iin instanceof ICartagoCallbackRemote))) {
				return ((ICartagoCallbackRemote)iin);
			}
			return new Proxy(obj);
		}

		public IBinder asBinder(){
			return this;
		}

		@Override
		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws RemoteException {
			switch (code){
			case INTERFACE_TRANSACTION:
			{
				reply.writeString(DESCRIPTOR);
				return true;
			}
			case TRANSACTION_notifyCartagoEvent:
			{
				data.enforceInterface(DESCRIPTOR);
				CartagoEvent ev = (CartagoEvent) data.readSerializable();
				this.notifyCartagoEvent(ev);
				reply.writeNoException();
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		static final int TRANSACTION_notifyCartagoEvent = (IBinder.FIRST_CALL_TRANSACTION + 0);

		private static class Proxy implements ICartagoCallbackRemote {
			private IBinder mRemote;
			Proxy(IBinder remote) {
				mRemote = remote;
			}

			public IBinder asBinder() {
				return mRemote;
			}

			public String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			@Override
			public void notifyCartagoEvent(CartagoEvent ev)
					throws RemoteException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeSerializable(ev);
					mRemote.transact(Stub.TRANSACTION_notifyCartagoEvent, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}
		}
	}

}
