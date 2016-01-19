package org.mightyfish.operator.bc;

import java.io.IOException;

import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.crypto.AsymmetricBlockCipher;
import org.mightyfish.crypto.encodings.PKCS1Encoding;
import org.mightyfish.crypto.engines.RSAEngine;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.crypto.util.PublicKeyFactory;

public class BcRSAAsymmetricKeyWrapper
    extends BcAsymmetricKeyWrapper
{
    public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter publicKey)
    {
        super(encAlgId, publicKey);
    }

    public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier encAlgId, SubjectPublicKeyInfo publicKeyInfo)
        throws IOException
    {
        super(encAlgId, PublicKeyFactory.createKey(publicKeyInfo));
    }

    protected AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier algorithm)
    {
        return new PKCS1Encoding(new RSAEngine());
    }
}
