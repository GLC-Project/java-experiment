package coin;

import util.BigFastList;
/*
 * @author Amir Eslampanah
 * 
 */

public class Wallet {

    /**
     * This class handles public and private keys and general address management
     * as well as wallet encryption.
     * 
     */
    BigFastList<AddressKeyPair> addressKeyPairs = new BigFastList<AddressKeyPair>();

    /**
     * 
     */
    public Wallet() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param addressKeyPairs
     */
    public Wallet(BigFastList<AddressKeyPair> addressKeyPairs) {
	// TODO Auto-generated constructor stub
	this.addressKeyPairs = addressKeyPairs;
    }

    /**
     * @return the addressKeyPairs
     */
    public BigFastList<AddressKeyPair> getAddressKeyPairs() {
	return this.addressKeyPairs;
    }

    /**
     * @param addressKeyPairs
     *            the addressKeyPairs to set
     */
    public void setAddressKeyPairs(
	    BigFastList<AddressKeyPair> addressKeyPairs) {
	this.addressKeyPairs = addressKeyPairs;
    }

}
