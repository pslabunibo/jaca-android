package cartago.infrastructure.android;

import android.os.RemoteException;

import cartago.AgentBody;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.Op;
import cartago.WorkspaceId;

public class AgentBodyRemote extends IAgentBodyRemote.Stub {

	private AgentBody mCtx;
	private long mLastPingFromMind;
	
	public AgentBodyRemote(AgentBody ctx) {
		mCtx = ctx;
		mLastPingFromMind = System.currentTimeMillis();
	}
	
	@Override
	public void doAction(long actionId, ArtifactId id, Op op, IAlignmentTest test, long timeout) throws RemoteException, CartagoException {
		mCtx.doAction(actionId, id, op, test, timeout);
	}

	@Override
	public void doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws RemoteException, CartagoException {
		mCtx.doAction(actionId, name, op, test, timeout);
		
	}

	@Override
	public AgentId getAgentId() throws CartagoException, RemoteException {
		return mCtx.getAgentId();
	}

	@Override
	public WorkspaceId getWorkspaceId() throws RemoteException, CartagoException {
		return mCtx.getWorkspaceId();
	}

	@Override
	public synchronized void ping() throws RemoteException {
		mLastPingFromMind = System.currentTimeMillis();
	}
	
	public synchronized long getLastPing(){
		return mLastPingFromMind;
	}	
	
	public AgentBody getContext(){
		return mCtx;
	}

	@Override
	public ArtifactId getArtifactIdFromOp(Op op) throws RemoteException, CartagoException {
		return mCtx.getArtifactIdFromOp(op);
	}

	@Override
	public ArtifactId getArtifactIdFromOp(String name, Op op) throws RemoteException, CartagoException {
		return mCtx.getArtifactIdFromOp(name,op);
	}
}
