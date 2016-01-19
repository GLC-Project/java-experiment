package org.mightyfish.operator;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculatorProvider
{
    DigestCalculator get(AlgorithmIdentifier digestAlgorithmIdentifier)
        throws OperatorCreationException;
}
