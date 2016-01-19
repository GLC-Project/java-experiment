package util.events;

import util.BigFastList;

/**
 * @author Amir Eslampanah
 * 
 */
public class EventManager {
    private final BigFastList<Event> queue;

    /**
     * 
     */
    public EventManager() {
	this.queue = new BigFastList<Event>();

    }

    /**
     * @return the queue
     */
    public BigFastList<Event> getQueue() {
	return this.queue;
    }
}
