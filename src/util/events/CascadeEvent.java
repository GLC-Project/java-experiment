package util.events;

import java.util.BigFastList;

/**
 * A cascade event is a list of events that have the following qualities: 1:
 * They must be executed in order. 2: They must all be executed correctly before
 * any writes occur to disk. 3: They must all be executed correctly before any
 * changes to main program variables occur. (Thus these events only work on a
 * copy of the main block chain, this is streamed using read-only NIO from disk)
 * 
 * @author A
 * 
 */
public class CascadeEvent {

    private BigFastList<Event> cascade;

    /**
	 * 
	 */
    public CascadeEvent() {
	this.cascade = new BigFastList<Event>();
    }

    /**
     * @return the cascade
     */
    public BigFastList<Event> getCascade() {
	return this.cascade;
    }

    /**
     * @param cascade1
     *            the cascade to set
     */
    public void setCascade(BigFastList<Event> cascade1) {
	this.cascade = cascade1;
    }

    /**
     * @param e
     *            the event to add to the cascade
     */
    public void addEvent(Event e) {
	this.cascade.add(e);
    }

    /**
     * Run this cascade
     * 
     * @return Whether this cascade was executed successfully or not
     */
    public boolean execute() {
	// Execute each event
	for (Event e : this.cascade) {
	    if (!e.execute()) {
		// If any event in the cascade fails.. mark the entire cascade
		// as a failure.
		return false;
	    }
	}
	// If successful return true, otherwise return false
	return true;
    }
}
