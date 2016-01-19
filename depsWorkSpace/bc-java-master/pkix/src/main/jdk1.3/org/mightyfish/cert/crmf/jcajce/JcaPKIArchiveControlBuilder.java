package org.mightyfish.cert.crmf.jcajce;

import java.security.PrivateKey;

import org.mightyfish.asn1.pkcs.PrivateKeyInfo;
import org.mightyfish.asn1.x500.X500Name;
import org.mightyfish.asn1.x509.GeneralName;
import org.mightyfish.cert.crmf.PKIArchiveControlBuilder;

public class JcaPKIArchiveControlBuilder
    extends PKIArchiveControlBuilder
{
    public JcaPKIArchiveControlBuilder(PrivateKey privateKey, X500Name name)
    {
        this(privateKey, new GeneralName(name));
    }

    public JcaPKIArchiveControlBuilder(PrivateKey privateKey, GeneralName generalName)
    {
        super(PrivateKeyInfo.getInstance(privateKey.getEncoded()), generalName);
    }
}
