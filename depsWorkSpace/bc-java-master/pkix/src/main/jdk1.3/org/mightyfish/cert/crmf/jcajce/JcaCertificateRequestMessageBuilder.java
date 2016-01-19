package org.mightyfish.cert.crmf.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;

import org.mightyfish.asn1.x500.X500Name;
import org.mightyfish.asn1.x509.GeneralName;
import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.cert.crmf.CertificateRequestMessageBuilder;

public class JcaCertificateRequestMessageBuilder
    extends CertificateRequestMessageBuilder
{
    public JcaCertificateRequestMessageBuilder(BigInteger certReqId)
    {
        super(certReqId);
    }

    public JcaCertificateRequestMessageBuilder setPublicKey(PublicKey publicKey)
    {
        setPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));

        return this;
    }
}
