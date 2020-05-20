package cartago.infrastructure.android;

import android.content.ServiceConnection;

public class AndroidAgentBodyProxyWrapper {

	private AgentBodyProxy proxy;
	private ServiceConnection connection;
	
	public AndroidAgentBodyProxyWrapper(AgentBodyProxy proxy, ServiceConnection connection) {
		super();
		this.proxy = proxy;
		this.connection = connection;
	}
	
	public AgentBodyProxy getProxy() {
		return proxy;
	}
	
	public ServiceConnection getConnection() {
		return connection;
	}
}