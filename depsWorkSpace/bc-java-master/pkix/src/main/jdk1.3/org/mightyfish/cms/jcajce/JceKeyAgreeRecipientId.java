package org.mightyfish.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.mightyfish.jce.PrincipalUtil;
import org.mightyfish.jce.X509Principal;

import org.mightyfish.asn1.x500.X500Name;
import org.mightyfish.cms.KeyAgreeRecipientId;

public class JceKeyAgreeRecipientId
    extends KeyAgreeRecipientId
{
    public JceKeyAgreeRecipientId(X509Certificate certificate)
    {
        super(X500Name.getInstance(extractIssuer(certificate)), certificate.getSerialNumber());
    }

    private static X509Principal extractIssuer(X509Certificate certificate)
    {
        try
        {
            return PrincipalUtil.getIssuerX509Principal(certificate);
        }
        catch (CertificateEncodingException e)
        {
            throw new IllegalStateException("can't extract issuer");
        }
    }
}
