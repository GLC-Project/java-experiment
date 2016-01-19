package org.mightyfish.openpgp.operator;

import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPPublicKey;

public interface PGPContentVerifierBuilder
{
    public PGPContentVerifier build(final PGPPublicKey publicKey)
        throws PGPException;
}
