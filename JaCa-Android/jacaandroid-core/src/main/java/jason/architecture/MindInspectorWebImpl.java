package jason.architecture;

import org.w3c.dom.Document;
import jason.asSemantics.Agent;

/**
 * NOTE: This class is used at runtime, via reflection.
 */

@SuppressWarnings("unused")
public class MindInspectorWebImpl extends MindInspectorWeb {

    public MindInspectorWebImpl() { }
    
    public synchronized String startHttpServer()  {
    	return "";
    }

    /**
     * Adds the agent in the list of available agents for mind inspection
     * @param ag the involved agent
     */
    public synchronized void registerAg(Agent ag) { }

    /**
     * Removes the agent from the list of available agents for mind inspection
     * @param ag the involved agent
     */
    public synchronized void removeAg(Agent ag) { }
    
    public synchronized void addAgState(Agent ag, Document mind, boolean hasHistory) { }
}
