package org.mightyfish.cms.jcajce;

import org.mightyfish.jce.cert.X509CertSelector;

import org.mightyfish.cms.KeyTransRecipientId;
import org.mightyfish.cms.SignerId;

public class JcaX509CertSelectorConverter
    extends org.mightyfish.cert.selector.jcajce.JcaX509CertSelectorConverter
{
    public JcaX509CertSelectorConverter()
    {
    }

    public X509CertSelector getCertSelector(KeyTransRecipientId recipientId)
    {
        return doConversion(recipientId.getIssuer(), recipientId.getSerialNumber(), recipientId.getSubjectKeyIdentifier());
    }

    public X509CertSelector getCertSelector(SignerId signerId)
    {
        return doConversion(signerId.getIssuer(), signerId.getSerialNumber(), signerId.getSubjectKeyIdentifier());
    }
}
