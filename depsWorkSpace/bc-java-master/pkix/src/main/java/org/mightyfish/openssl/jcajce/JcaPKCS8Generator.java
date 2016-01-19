package org.mightyfish.openssl.jcajce;

import java.security.PrivateKey;

import org.mightyfish.asn1.pkcs.PrivateKeyInfo;
import org.mightyfish.openssl.PKCS8Generator;
import org.mightyfish.operator.OutputEncryptor;
import org.mightyfish.util.io.pem.PemGenerationException;

public class JcaPKCS8Generator
    extends PKCS8Generator
{
    public JcaPKCS8Generator(PrivateKey key, OutputEncryptor encryptor)
         throws PemGenerationException
    {
         super(PrivateKeyInfo.getInstance(key.getEncoded()), encryptor);
    }
}
