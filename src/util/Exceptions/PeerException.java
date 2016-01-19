package util.Exceptions;

/**
 * Thrown when a problem occurs in communicating with a peer, and we should
 * retry.
 */
public class PeerException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 67173852164072031L;

    /**
     * @param msg
     */
    public PeerException(String msg) {
	super(msg);
    }

    /**
     * @param e
     */
    public PeerException(Exception e) {
	super(e);
    }

    /**
     * @param msg
     * @param e
     */
    public PeerException(String msg, Exception e) {
	super(msg, e);
    }
}
