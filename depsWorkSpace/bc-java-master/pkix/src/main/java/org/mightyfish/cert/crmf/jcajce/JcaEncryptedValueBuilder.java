package org.mightyfish.cert.crmf.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.mightyfish.asn1.crmf.EncryptedValue;
import org.mightyfish.cert.crmf.CRMFException;
import org.mightyfish.cert.crmf.EncryptedValueBuilder;
import org.mightyfish.cert.jcajce.JcaX509CertificateHolder;
import org.mightyfish.operator.KeyWrapper;
import org.mightyfish.operator.OutputEncryptor;

public class JcaEncryptedValueBuilder
    extends EncryptedValueBuilder
{
    public JcaEncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor)
    {
        super(wrapper, encryptor);
    }

    public EncryptedValue build(X509Certificate certificate)
        throws CertificateEncodingException, CRMFException
    {
        return build(new JcaX509CertificateHolder(certificate));
    }
}
