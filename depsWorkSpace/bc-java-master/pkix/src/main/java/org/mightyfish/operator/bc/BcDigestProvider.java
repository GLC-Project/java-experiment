package org.mightyfish.operator.bc;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.ExtendedDigest;
import org.mightyfish.operator.OperatorCreationException;

public interface BcDigestProvider
{
    ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier)
        throws OperatorCreationException;
}
