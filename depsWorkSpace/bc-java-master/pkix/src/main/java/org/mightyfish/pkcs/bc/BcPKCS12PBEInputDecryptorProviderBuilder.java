package org.mightyfish.pkcs.bc;

import java.io.InputStream;

import org.mightyfish.asn1.pkcs.PKCS12PBEParams;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.crypto.ExtendedDigest;
import org.mightyfish.crypto.digests.SHA1Digest;
import org.mightyfish.crypto.generators.PKCS12ParametersGenerator;
import org.mightyfish.crypto.io.CipherInputStream;
import org.mightyfish.crypto.paddings.PaddedBufferedBlockCipher;
import org.mightyfish.operator.GenericKey;
import org.mightyfish.operator.InputDecryptor;
import org.mightyfish.operator.InputDecryptorProvider;

public class BcPKCS12PBEInputDecryptorProviderBuilder
{
    private ExtendedDigest digest;

    public BcPKCS12PBEInputDecryptorProviderBuilder()
    {
         this(new SHA1Digest());
    }

    public BcPKCS12PBEInputDecryptorProviderBuilder(ExtendedDigest digest)
    {
         this.digest = digest;
    }

    public InputDecryptorProvider build(final char[] password)
    {
        return new InputDecryptorProvider()
        {
            public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier)
            {
                final PaddedBufferedBlockCipher engine = PKCS12PBEUtils.getEngine(algorithmIdentifier.getAlgorithm());

                PKCS12PBEParams           pbeParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());

                CipherParameters params = PKCS12PBEUtils.createCipherParameters(algorithmIdentifier.getAlgorithm(), digest, engine.getBlockSize(), pbeParams, password);

                engine.init(false, params);

                return new InputDecryptor()
                {
                    public AlgorithmIdentifier getAlgorithmIdentifier()
                    {
                        return algorithmIdentifier;
                    }

                    public InputStream getInputStream(InputStream input)
                    {
                        return new CipherInputStream(input, engine);
                    }

                    public GenericKey getKey()
                    {
                        return new GenericKey(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
                    }
                };
            }
        };

    }
}
