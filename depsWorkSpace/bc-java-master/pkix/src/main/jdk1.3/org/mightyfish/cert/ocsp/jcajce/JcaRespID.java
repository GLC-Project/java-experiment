package org.mightyfish.cert.ocsp.jcajce;

import java.security.PublicKey;

import org.mightyfish.asn1.x500.X500Name;
import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.cert.ocsp.OCSPException;
import org.mightyfish.cert.ocsp.RespID;
import org.mightyfish.operator.DigestCalculator;

public class JcaRespID
    extends RespID
{
    public JcaRespID(PublicKey pubKey, DigestCalculator digCalc)
        throws OCSPException
    {
        super(SubjectPublicKeyInfo.getInstance(pubKey.getEncoded()), digCalc);
    }
}
