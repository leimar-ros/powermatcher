package net.powermatcher.core.connectivity;

import net.powermatcher.api.Session;
import net.powermatcher.api.connectivity.AgentEndpointProxy;
import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.MarketBasis;
import net.powermatcher.api.data.Price;
import net.powermatcher.core.BaseAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAgentEndpointProxy extends BaseAgent implements AgentEndpointProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAgentEndpointProxy.class);

    private String matcherEndpointProxyId;
    
	private Session localSession;

	@Override
	public boolean isLocalConnected() {
		return this.localSession != null;
	}

    @Override
    public void updateLocalBid(Bid newBid) {
    	if (!this.isLocalConnected()) {
			LOGGER.warn("Desired parent agent not connected, skip sendingg bid update");
			return;
    	}

		this.localSession.updateBid(newBid);
		return;
    }

    public void setMatcherEndpointProxyId(String matcherEndpointProxyId) {
		this.matcherEndpointProxyId = matcherEndpointProxyId;
	}

    public String getMatcherEndpointProxyId() {
		return this.matcherEndpointProxyId;
	}

    public MarketBasis getLocalMarketBasis() {
    	if (this.isLocalConnected()) {
        	return this.localSession.getMarketBasis();
    	}
    	
    	return null;
    }
    
	@Override
	public void connectToMatcher(Session session) {
        this.setClusterId(session.getClusterId());
        this.localSession = session;
	}

	@Override
	public void matcherEndpointDisconnected(Session session) {
		this.localSession = null;
	}

	@Override
	public synchronized void updatePrice(Price newPrice) {
		if (!this.isRemoteConnected()) {
			LOGGER.warn("Remote agent not connected, skip sending price update");
			return;
		}		
		
		LOGGER.info("Sending price update to remote agent {}", newPrice);
		
		this.updateRemotePrice(newPrice);
	}
}