package org.mightyfish.operator.bc;

import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.pkcs.PKCSObjectIdentifiers;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.AsymmetricBlockCipher;
import org.mightyfish.crypto.InvalidCipherTextException;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.operator.AsymmetricKeyUnwrapper;
import org.mightyfish.operator.GenericKey;
import org.mightyfish.operator.OperatorException;

public abstract class BcAsymmetricKeyUnwrapper
    extends AsymmetricKeyUnwrapper
{
    private AsymmetricKeyParameter privateKey;

    public BcAsymmetricKeyUnwrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter privateKey)
    {
        super(encAlgId);

        this.privateKey = privateKey;
    }

    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey)
        throws OperatorException
    {
        AsymmetricBlockCipher keyCipher = createAsymmetricUnwrapper(this.getAlgorithmIdentifier().getAlgorithm());

        keyCipher.init(false, privateKey);
        try
        {
            byte[] key = keyCipher.processBlock(encryptedKey, 0, encryptedKey.length);

            if (encryptedKeyAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.des_EDE3_CBC))
            {
                return new GenericKey(encryptedKeyAlgorithm, key);
            }
            else
            {
                return new GenericKey(encryptedKeyAlgorithm, key);
            }
        }
        catch (InvalidCipherTextException e)
        {
            throw new OperatorException("unable to recover secret key: " + e.getMessage(), e);
        }
    }

    protected abstract AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier algorithm);
}
