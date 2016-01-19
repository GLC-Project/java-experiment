package org.mightyfish.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;

import org.mightyfish.jcajce.util.DefaultJcaJceHelper;
import org.mightyfish.jcajce.util.JcaJceHelper;
import org.mightyfish.jcajce.util.NamedJcaJceHelper;
import org.mightyfish.jcajce.util.ProviderJcaJceHelper;
import org.mightyfish.openssl.PEMEncryptor;
import org.mightyfish.openssl.PEMException;

public class JcePEMEncryptorBuilder
{
    private final String algorithm;

    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private SecureRandom random;

    public JcePEMEncryptorBuilder(String algorithm)
    {
        this.algorithm = algorithm;
    }

    public JcePEMEncryptorBuilder setProvider(Provider provider)
    {
        this.helper = new ProviderJcaJceHelper(provider);

        return this;
    }

    public JcePEMEncryptorBuilder setProvider(String providerName)
    {
        this.helper = new NamedJcaJceHelper(providerName);

        return this;
    }

    public JcePEMEncryptorBuilder setSecureRandom(SecureRandom random)
    {
        this.random = random;

        return this;
    }

    public PEMEncryptor build(final char[] password)
    {
        if (random == null)
        {
            random = new SecureRandom();
        }

        int ivLength = algorithm.startsWith("AES-") ? 16 : 8;

        final byte[] iv = new byte[ivLength];

        random.nextBytes(iv);

        return new PEMEncryptor()
        {
            public String getAlgorithm()
            {
                return algorithm;
            }

            public byte[] getIV()
            {
                return iv;
            }

            public byte[] encrypt(byte[] encoding)
                throws PEMException
            {
                return PEMUtilities.crypt(true, helper, encoding, password, algorithm, iv);
            }
        };
    }
}
