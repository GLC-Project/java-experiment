package coin;

import util.crypto.Address;
import util.crypto.ECKey;
import util.crypto.EncryptedPrivateKey;

/**
 * @author Amir Eslampanah
 * 
 */
public class AddressKeyPair {

    private Address address;
    private StringBuffer label;
    private ECKey pubKey;
    private EncryptedPrivateKey privKey;
    private final boolean recipient;

    /**
     * @param recipient1
     * 
     */
    public AddressKeyPair(boolean recipient1) {
	this.recipient = recipient1;
    }

    /**
     * @return the address
     */
    public Address getAddress() {
	return this.address;
    }

    /**
     * @param address1
     *            the address to set
     */
    public void setAddress(Address address1) {
	this.address = address1;
    }

    /**
     * @return the pubKey
     */
    public ECKey getPubKey() {
	return this.pubKey;
    }

    /**
     * @param pubKey1
     *            the pubKey to set
     */
    public void setPubKey(ECKey pubKey1) {
	this.pubKey = pubKey1;
    }

    /**
     * @return the privKey
     */
    public EncryptedPrivateKey getPrivKey() {
	return this.privKey;
    }

    /**
     * @param privKey1
     *            the privKey to set
     */
    public void setPrivKey(EncryptedPrivateKey privKey1) {
	this.privKey = privKey1;
    }

    /**
     * @return the label
     */
    public StringBuffer getLabel() {
	return this.label;
    }

    /**
     * @param label1
     *            the label to set
     */
    public void setLabel(StringBuffer label1) {
	this.label = label1;
    }

    /**
     * @return the recipient
     */
    public boolean isRecipient() {
	return this.recipient;
    }

}
