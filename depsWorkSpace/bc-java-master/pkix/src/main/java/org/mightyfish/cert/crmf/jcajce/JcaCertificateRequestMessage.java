package org.mightyfish.cert.crmf.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;

import javax.security.auth.x500.X500Principal;

import org.mightyfish.asn1.ASN1Encoding;
import org.mightyfish.asn1.crmf.CertReqMsg;
import org.mightyfish.asn1.x500.X500Name;
import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.cert.crmf.CRMFException;
import org.mightyfish.cert.crmf.CertificateRequestMessage;
import org.mightyfish.jcajce.util.DefaultJcaJceHelper;
import org.mightyfish.jcajce.util.NamedJcaJceHelper;
import org.mightyfish.jcajce.util.ProviderJcaJceHelper;

public class JcaCertificateRequestMessage
    extends CertificateRequestMessage
{
    private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());

    public JcaCertificateRequestMessage(byte[] certReqMsg)
    {
        this(CertReqMsg.getInstance(certReqMsg));
    }

    public JcaCertificateRequestMessage(CertificateRequestMessage certReqMsg)
    {
        this(certReqMsg.toASN1Structure());
    }

    public JcaCertificateRequestMessage(CertReqMsg certReqMsg)
    {
        super(certReqMsg);
    }

    public JcaCertificateRequestMessage setProvider(String providerName)
    {
        this.helper = new CRMFHelper(new NamedJcaJceHelper(providerName));

        return this;
    }

    public JcaCertificateRequestMessage setProvider(Provider provider)
    {
        this.helper = new CRMFHelper(new ProviderJcaJceHelper(provider));

        return this;
    }

    public X500Principal getSubjectX500Principal()
    {
        X500Name subject = this.getCertTemplate().getSubject();

        if (subject != null)
        {
            try
            {
                return new X500Principal(subject.getEncoded(ASN1Encoding.DER));
            }
            catch (IOException e)
            {
                throw new IllegalStateException("unable to construct DER encoding of name: " + e.getMessage());
            }
        }

        return null;
    }

    public PublicKey getPublicKey()
        throws CRMFException
    {
        SubjectPublicKeyInfo subjectPublicKeyInfo = getCertTemplate().getPublicKey();

        if (subjectPublicKeyInfo != null)
        {
            return helper.toPublicKey(subjectPublicKeyInfo);
        }

        return null;
    }
}
