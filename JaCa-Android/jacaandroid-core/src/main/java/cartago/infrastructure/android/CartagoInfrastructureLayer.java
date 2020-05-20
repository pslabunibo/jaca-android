package cartago.infrastructure.android;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import java.util.concurrent.ConcurrentLinkedQueue;

import cartago.AgentCredential;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoContext;
import cartago.ICartagoLogger;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;
import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.infrastructure.ICartagoInfrastructureLayer;
import it.unibo.pslab.jaca_android.MasService;


public class CartagoInfrastructureLayer implements ICartagoInfrastructureLayer {

	private KeepRemoteBodyAliveManagerAgent mKeepAliveAgent;
	private ConcurrentLinkedQueue<AndroidAgentBodyProxyWrapper> mRemoteCtxs;
	private CartagoNodeRemote mService;
	private static final int DEF_WSP_WAIT_TIMEOUT = 2000;
	
	public CartagoInfrastructureLayer(){
		mRemoteCtxs = new ConcurrentLinkedQueue<>();
		mKeepAliveAgent = new KeepRemoteBodyAliveManagerAgent(mRemoteCtxs,500);
		mKeepAliveAgent.start();
	}
	
	@Override
	public OpId execRemoteInterArtifactOp(ICartagoCallback callback,
			long callbackId, AgentId userId, ArtifactId srcId,
			ArtifactId targetId, String address, Op op, long timeout,
			IAlignmentTest test) throws
			CartagoException {
		try {
			CartagoCallbackRemote srv = new CartagoCallbackRemote(callback);
			CartagoCallbackProxy proxy = new CartagoCallbackProxy(srv);
			ICartagoNodeRemote env = _getCartagoNodeRemote(address);
			return env.execInterArtifactOp(proxy, callbackId, userId, srcId, targetId, op, timeout, test);
		} catch (RemoteException ex){
			ex.printStackTrace();
			throw new CartagoException("Inter-artifact op failed: "+ex.getLocalizedMessage());
		}
	}

	@Override
	public NodeId getNodeAt(String address)
			throws CartagoException {
		
		try {
			ICartagoNodeRemote env = _getCartagoNodeRemote(address);
			return env.getNodeId();		
		} catch (RemoteException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} 
	}

	@Override
	public boolean isServiceRunning() {
		return mService != null;
	}

	@Override
	public ICartagoContext joinRemoteWorkspace(String wspName, String address, AgentCredential cred, ICartagoCallback eventListener) throws CartagoInfrastructureLayerException{
		JoinWspConnection joinConn = new JoinWspConnection();
		ICartagoNodeRemote cartagoRemoteNode = _getCartagoNodeRemote(address, joinConn);
		ICartagoContext ctx = null;
		
		if(cartagoRemoteNode==null){
			throw new CartagoInfrastructureLayerException();
		} else {
			CartagoCallbackRemote callbackRemote = null;
			CartagoCallbackProxy proxy = null;
			try {
				callbackRemote = new CartagoCallbackRemote(eventListener);
				proxy = new CartagoCallbackProxy(callbackRemote);
				/* We have to wait some seconds to allow the creation of the workspace to be joined */
				Thread.sleep(DEF_WSP_WAIT_TIMEOUT);
				ctx = cartagoRemoteNode.join(wspName, cred, proxy);
				mRemoteCtxs.add(new AndroidAgentBodyProxyWrapper((AgentBodyProxy)ctx, joinConn));
			} catch (Exception ex) {
				/*
				 * We check if the exception was due because the workspace was not created yet.
				 * In case of JaCa-Service spawned dynamically 
				 */
				try{
					ctx = cartagoRemoteNode.join(wspName, cred, proxy);
					mRemoteCtxs.add(new AndroidAgentBodyProxyWrapper((AgentBodyProxy)ctx, joinConn));
				}catch (Exception e) {
					throw new CartagoInfrastructureLayerException();
				}
			} 
		}
		return ctx;
	}

	@Override
	public void shutdownLayer() throws CartagoException {
		mKeepAliveAgent.shutdown();
	}

	@Override
	public void shutdownService() throws CartagoException {
		if (mService != null){
			mService.shutdownService();
			mService = null;
		}
	}

	@Override
	public void startService(CartagoNode node, String address)
			throws CartagoInfrastructureLayerException {
		
		if (mService != null){
			throw new CartagoInfrastructureLayerException();
		}
		try {
			mService = new CartagoNodeRemote(node);
			if (address == null || address.equals("")){
				address = MasService.getInstance().getApplication().getPackageName();
			} 
			mService.install(address);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
	}
	
	
	private ICartagoNodeRemote _getCartagoNodeRemote(String address, JoinWspConnection joinConn) {
		Intent service = new Intent(address);
		service.putExtra("onBindAction", "join_wsp");
		MasService.getInstance().bindService(service, joinConn, Context.BIND_AUTO_CREATE);
		joinConn.waitForServiceBinding();
		return joinConn.getCartagoNodeRemote();
	}

	private ICartagoNodeRemote _getCartagoNodeRemote(String address) {
		Intent service = new Intent(address);
		service.putExtra("onBindAction", "join_wsp");
		JoinWspConnection joinConn = new JoinWspConnection();
		MasService.getInstance().bindService(service, joinConn, Context.BIND_AUTO_CREATE);
		//We wait for waitSeconds seconds for connection
		joinConn.waitForServiceBinding();
		return joinConn.getCartagoNodeRemote();
	}

	@Override
	public void registerLoggerToRemoteWsp(String wspName, String address, ICartagoLogger logger) throws CartagoException {
		// TODO 
	}
}