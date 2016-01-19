package org.mightyfish.pkcs;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.operator.MacCalculator;
import org.mightyfish.operator.OperatorCreationException;

public interface PKCS12MacCalculatorBuilder
{
    MacCalculator build(char[] password)
        throws OperatorCreationException;

    AlgorithmIdentifier getDigestAlgorithmIdentifier();
}
