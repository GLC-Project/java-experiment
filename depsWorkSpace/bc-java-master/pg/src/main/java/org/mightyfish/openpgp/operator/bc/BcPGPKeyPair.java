package org.mightyfish.openpgp.operator.bc;

import java.util.Date;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPKeyPair;
import org.mightyfish.openpgp.PGPPrivateKey;
import org.mightyfish.openpgp.PGPPublicKey;

public class BcPGPKeyPair
    extends PGPKeyPair
{
    private static PGPPublicKey getPublicKey(int algorithm, AsymmetricKeyParameter pubKey, Date date)
        throws PGPException
    {
        return new BcPGPKeyConverter().getPGPPublicKey(algorithm, pubKey, date);
    }

    private static PGPPrivateKey getPrivateKey(PGPPublicKey pub, AsymmetricKeyParameter privKey)
        throws PGPException
    {
        return new BcPGPKeyConverter().getPGPPrivateKey(pub, privKey);
    }

    public BcPGPKeyPair(int algorithm, AsymmetricCipherKeyPair keyPair, Date date)
        throws PGPException
    {
        this.pub = getPublicKey(algorithm, keyPair.getPublic(), date);
        this.priv = getPrivateKey(this.pub, keyPair.getPrivate());
    }
}
