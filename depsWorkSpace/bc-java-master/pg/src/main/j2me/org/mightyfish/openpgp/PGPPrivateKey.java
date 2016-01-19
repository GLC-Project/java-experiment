package org.mightyfish.openpgp;

import org.mightyfish.bcpg.BCPGKey;
import org.mightyfish.bcpg.DSASecretBCPGKey;
import org.mightyfish.bcpg.ElGamalSecretBCPGKey;
import org.mightyfish.bcpg.PublicKeyPacket;
import org.mightyfish.bcpg.RSASecretBCPGKey;

/**
 * general class to contain a private key for use with other openPGP
 * objects.
 */
public class PGPPrivateKey
{
    private long          keyID;
    private PublicKeyPacket publicKeyPacket;
    private BCPGKey privateKeyDataPacket;

    public PGPPrivateKey(
        long              keyID,
        PublicKeyPacket   publicKeyPacket,
        BCPGKey           privateKeyDataPacket)
    {
        this.keyID = keyID;
        this.publicKeyPacket = publicKeyPacket;
        this.privateKeyDataPacket = privateKeyDataPacket;
    }

    /**
     * Return the keyID associated with the contained private key.
     * 
     * @return long
     */
    public long getKeyID()
    {
        return keyID;
    }
    
    public PublicKeyPacket getPublicKeyPacket()
    {
        return publicKeyPacket;
    }

    public BCPGKey getPrivateKeyDataPacket()
    {
        return privateKeyDataPacket;
    }
}
