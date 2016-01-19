package org.mightyfish.cert.crmf;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.operator.InputDecryptor;

public interface ValueDecryptorGenerator
{
    InputDecryptor getValueDecryptor(AlgorithmIdentifier keyAlg, AlgorithmIdentifier symmAlg, byte[] encKey)
        throws CRMFException;
}
