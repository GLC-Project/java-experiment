package org.mightyfish.pkcs.bc;

import java.io.OutputStream;
import java.security.SecureRandom;

import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.pkcs.PKCS12PBEParams;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.BlockCipher;
import org.mightyfish.crypto.BufferedBlockCipher;
import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.crypto.ExtendedDigest;
import org.mightyfish.crypto.digests.SHA1Digest;
import org.mightyfish.crypto.generators.PKCS12ParametersGenerator;
import org.mightyfish.crypto.io.CipherOutputStream;
import org.mightyfish.crypto.paddings.PKCS7Padding;
import org.mightyfish.crypto.paddings.PaddedBufferedBlockCipher;
import org.mightyfish.operator.GenericKey;
import org.mightyfish.operator.OutputEncryptor;

public class BcPKCS12PBEOutputEncryptorBuilder
{
    private ExtendedDigest digest;

    private BufferedBlockCipher engine;
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine)
    {
        this(algorithm, engine, new SHA1Digest());
    }

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine, ExtendedDigest pbeDigest)
    {
        this.algorithm = algorithm;
        this.engine = new PaddedBufferedBlockCipher(engine, new PKCS7Padding());
        this.digest = pbeDigest;
    }

    public OutputEncryptor build(final char[] password)
    {
        if (random == null)
        {
            random = new SecureRandom();
        }

        final byte[] salt = new byte[20];
        final int    iterationCount = 1024;

        random.nextBytes(salt);

        final PKCS12PBEParams pbeParams = new PKCS12PBEParams(salt, iterationCount);

        CipherParameters params = PKCS12PBEUtils.createCipherParameters(algorithm, digest, engine.getBlockSize(), pbeParams, password);

        engine.init(true, params);

        return new OutputEncryptor()
        {
            public AlgorithmIdentifier getAlgorithmIdentifier()
            {
                return new AlgorithmIdentifier(algorithm, pbeParams);
            }

            public OutputStream getOutputStream(OutputStream out)
            {
                return new CipherOutputStream(out, engine);
            }

            public GenericKey getKey()
            {
                return new GenericKey(new AlgorithmIdentifier(algorithm, pbeParams), PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
            }
        };
    }
}
