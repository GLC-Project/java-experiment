package org.mightyfish.cert;

import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.operator.ContentVerifierProvider;
import org.mightyfish.operator.OperatorCreationException;

public interface X509ContentVerifierProviderBuilder
{
    ContentVerifierProvider build(SubjectPublicKeyInfo validatingKeyInfo)
        throws OperatorCreationException;

    ContentVerifierProvider build(X509CertificateHolder validatingKeyInfo)
        throws OperatorCreationException;
}
