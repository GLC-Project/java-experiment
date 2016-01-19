package org.mightyfish.cert.ocsp.jcajce;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.mightyfish.cert.jcajce.JcaX509CertificateHolder;
import org.mightyfish.cert.ocsp.CertificateID;
import org.mightyfish.cert.ocsp.OCSPException;
import org.mightyfish.operator.DigestCalculator;

public class JcaCertificateID
    extends CertificateID
{
    public JcaCertificateID(DigestCalculator digestCalculator, X509Certificate issuerCert, BigInteger number)
        throws OCSPException, CertificateEncodingException
    {
        super(digestCalculator, new JcaX509CertificateHolder(issuerCert), number);
    }
}
