package cartago.infrastructure.android;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.io.Serializable;

import cartago.AgentCredential;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoContext;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;

public interface ICartagoNodeRemote extends IInterface {

	ICartagoContext join(String wspName, AgentCredential cred, ICartagoCallback callback) throws RemoteException, CartagoException;
	void quit(String wspName, AgentId id) throws RemoteException, CartagoException;
	OpId execInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws RemoteException, CartagoException;
	String getVersion() throws CartagoException, RemoteException;
	NodeId getNodeId() throws CartagoException, RemoteException;
	
	/** Local-side IPC implementation stub class. */
	abstract class Stub extends android.os.Binder implements ICartagoNodeRemote {
		private static final String DESCRIPTOR = "cartago.infrastructure.android.ICartagoNodeRemote";
		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}
		/**
		 * Cast an IBinder object into an ICartagoNodeRemote interface,
		 * generating a proxy if needed.
		 */
		public static ICartagoNodeRemote asInterface(IBinder obj) {
			if ((obj==null)) {
				return null;
			}
			IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin!=null)&&(iin instanceof ICartagoNodeRemote))) {
				return ((ICartagoNodeRemote)iin);
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
			case TRANSACTION_join:
			{
				data.enforceInterface(DESCRIPTOR);
				String wspName = data.readString();
				AgentCredential cred  = (AgentCredential) data.readSerializable();
				ICartagoCallback callback;
				if ((0!=data.readInt())) {
					callback = CartagoCallbackProxy.CREATOR.createFromParcel(data);
				} else {
					callback = null;
				}
				ICartagoContext context;
				try {
					context = this.join(wspName, cred, callback);
					reply.writeNoException();
					if (context!=null) {
						reply.writeInt(1);
						((Parcelable)context).writeToParcel(reply, 0);
					} else {
						reply.writeInt(0);
					}
				} catch (CartagoException e) {
					reply.writeException(e);
				}
				return true;
			}
			case TRANSACTION_quit:
			{
				data.enforceInterface(DESCRIPTOR);
				String wspName = data.readString();
				AgentId id = (AgentId) data.readSerializable();
				try {
					this.quit(wspName, id);
					reply.writeNoException();
				} catch (CartagoException e) {
					reply.writeException(e);
				}
				return true;
			}
			case TRANSACTION_execInterArtifactOp:
			{
				data.enforceInterface(DESCRIPTOR);
				ICartagoCallback callback;
				if ((0!=data.readInt())) {
					callback = CartagoCallbackProxy.CREATOR.createFromParcel(data);
				} else {
					callback = null;
				}
				long callbackId = data.readLong();
				AgentId userId = (AgentId) data.readSerializable();
				ArtifactId srcId = (ArtifactId) data.readSerializable();
				ArtifactId targetId = (ArtifactId) data.readSerializable();
				Op op = (Op) data.readSerializable();
				long timeout = data.readLong();
				IAlignmentTest test = (IAlignmentTest) data.readSerializable();
				OpId opId;
				try {
					opId = this.execInterArtifactOp(callback, callbackId, userId, srcId, targetId, op, timeout, test);
					reply.writeNoException();
					reply.writeSerializable(opId);
				} catch (CartagoException e) {
					reply.writeException(e);
				}

				return true;
			}
			case TRANSACTION_getVersion:
			{
				data.enforceInterface(DESCRIPTOR);
				try {
					String version = this.getVersion();
					reply.writeNoException();
					reply.writeString(version);
				} catch (CartagoException e) {
					reply.writeException(e);
				}
				return true;
			}
			case TRANSACTION_getNodeId:
			{
				data.enforceInterface(DESCRIPTOR);
				try {
					NodeId id = this.getNodeId();
					reply.writeNoException();
					reply.writeSerializable(id);
				} catch (CartagoException e) {
					reply.writeException(e);
				}
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		static final int TRANSACTION_join = (IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_quit = (IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_execInterArtifactOp = (IBinder.FIRST_CALL_TRANSACTION + 2);
		static final int TRANSACTION_getVersion = (IBinder.FIRST_CALL_TRANSACTION + 3);
		static final int TRANSACTION_getNodeId = (IBinder.FIRST_CALL_TRANSACTION + 4);

		private static class Proxy implements ICartagoNodeRemote {
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
			public OpId execInterArtifactOp(ICartagoCallback callback,
					long callbackId, AgentId userId, ArtifactId srcId,
					ArtifactId targetId, Op op, long timeout,
					IAlignmentTest test) throws RemoteException,
					CartagoException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				OpId opId = null;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					if (callback!=null) {
						_data.writeInt(1);
						((Parcelable)callback).writeToParcel(_data, 0);
					} else {
						_data.writeInt(0);
					}
					_data.writeLong(callbackId);
					_data.writeSerializable(userId);
					_data.writeSerializable(srcId);
					_data.writeSerializable(targetId);
					_data.writeSerializable(op);
					_data.writeLong(timeout);
					_data.writeSerializable((Serializable)test);
					mRemote.transact(Stub.TRANSACTION_execInterArtifactOp, _data, _reply, 0);
					_reply.readException();
					opId = (OpId) _reply.readSerializable();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return opId;
			}

			@Override
			public String getVersion() throws CartagoException, RemoteException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				String version = null;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_getVersion, _data, _reply, 0);
					_reply.readException();
					version = _reply.readString();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return version;
			}

			@Override
			public ICartagoContext join(String wspName, AgentCredential cred,
					ICartagoCallback callback) throws RemoteException,
					CartagoException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				ICartagoContext context = null;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(wspName);
					_data.writeSerializable(cred);
					if (callback!=null) {
						_data.writeInt(1);
						((Parcelable)callback).writeToParcel(_data, 0);
					} else {
						_data.writeInt(0);
					}
					
					mRemote.transact(Stub.TRANSACTION_join, _data, _reply, 0);
					_reply.readException();
					if ((0!=_reply.readInt())) {
						context = AgentBodyProxy.CREATOR.createFromParcel(_reply);
					} else {
						context = null;
					}
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return context;
			}

			@Override
			public void quit(String wspName, AgentId id)
					throws RemoteException, CartagoException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(wspName);
					_data.writeSerializable(id);
					mRemote.transact(Stub.TRANSACTION_quit, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public NodeId getNodeId() throws CartagoException, RemoteException {
				
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				NodeId id = null;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_getNodeId, _data, _reply, 0);
					_reply.readException();
					id = (NodeId) _reply.readSerializable();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return id;
			}
		}
	}	
}
