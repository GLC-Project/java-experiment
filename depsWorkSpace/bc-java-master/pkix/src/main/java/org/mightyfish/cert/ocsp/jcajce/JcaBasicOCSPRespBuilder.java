package org.mightyfish.cert.ocsp.jcajce;

import java.security.PublicKey;

import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.cert.ocsp.BasicOCSPRespBuilder;
import org.mightyfish.cert.ocsp.OCSPException;
import org.mightyfish.operator.DigestCalculator;

public class JcaBasicOCSPRespBuilder
    extends BasicOCSPRespBuilder
{
    public JcaBasicOCSPRespBuilder(PublicKey key, DigestCalculator digCalc)
        throws OCSPException
    {
        super(SubjectPublicKeyInfo.getInstance(key.getEncoded()), digCalc);
    }
}
