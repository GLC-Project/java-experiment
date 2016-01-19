package org.mightyfish.openpgp.operator;

import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPPrivateKey;

public interface PGPContentSignerBuilder
{
    public PGPContentSigner build(final int signatureType, final PGPPrivateKey privateKey)
        throws PGPException;
}
