package cartago.infrastructure.android;

import android.os.RemoteException;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import cartago.AgentBody;
import cartago.AgentCredential;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CARTAGO_VERSION;
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.CartagoWorkspace;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoContext;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;
import it.unibo.pslab.jaca_android.core.JaCaBindingRole;

public class CartagoNodeRemote extends ICartagoNodeRemote.Stub {
	
	private String mFullAddress;
	private CartagoNode mNode;
	private ConcurrentLinkedQueue<AgentBodyRemote> mRemoteCtxs;
	private GarbageBodyCollectorAgent mGarbageCollector;
	
	
	public CartagoNodeRemote(CartagoNode node) {
		mNode = node;
		mRemoteCtxs = new ConcurrentLinkedQueue<AgentBodyRemote>();
		mGarbageCollector = new GarbageBodyCollectorAgent(mRemoteCtxs,500,1000);
		mGarbageCollector.start();
	}
	
	public void install(String address) throws Exception {
		/* WARNING: the  timeout - 1000 - must be greater than the 
		   delay used by the KeepRemoteContextAliveManager
		   to keep alive the remote contexts */

		mFullAddress = address;
		JaCaBindingRole.getInstance().addCartagoNodeRemote(mFullAddress, this);
		System.out.println("CArtAgO Android Service installed on address: "+mFullAddress);
	}
	
	public void shutdownService(){
		mGarbageCollector.stopActivity();
		try {
			JaCaBindingRole.getInstance().removeCartaNodeRemote(mFullAddress);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public String getAddress(){
		return mFullAddress;
	}
	
	@Override
	public OpId execInterArtifactOp(ICartagoCallback callback, long callbackId,
			AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op,
			long timeout, IAlignmentTest test) throws RemoteException,
			CartagoException {
		
		String wspName = targetId.getWorkspaceId().getName();
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		return wsp.execInterArtifactOp(callback, callbackId, userId, srcId, targetId, op, timeout, test);
	}

	@Override
	public String getVersion() throws CartagoException, RemoteException {
		return CARTAGO_VERSION.getID();
	}

	@Override
	public ICartagoContext join(String wspName, AgentCredential cred,
			ICartagoCallback callback) throws RemoteException, CartagoException {
		
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		//System.out.println("Remote request to join: "+wspName+" "+roleName+" "+cred+" "+callback);
		ICartagoContext ctx = wsp.join(cred,callback);
		AgentBodyRemote rctx = new AgentBodyRemote((AgentBody)ctx);
		mRemoteCtxs.add(rctx);
		AgentBodyProxy proxy = new AgentBodyProxy(rctx);
		return proxy;
	}

	@Override
	public void quit(String wspName, AgentId id) throws RemoteException,
			CartagoException {
		
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		wsp.getKernel().quitAgent(id);
		Iterator<AgentBodyRemote> it = mRemoteCtxs.iterator();
		while (it.hasNext()){
			AgentBodyRemote c = it.next();
			if (c.getAgentId().equals(id)){
				it.remove();
				break;
			}
		}
	}

	@Override
	public NodeId getNodeId() throws CartagoException, RemoteException {
		return mNode.getId();
	}
}