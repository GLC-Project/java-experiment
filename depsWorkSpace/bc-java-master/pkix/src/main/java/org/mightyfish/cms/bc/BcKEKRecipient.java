package org.mightyfish.cms.bc;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.cms.CMSException;
import org.mightyfish.cms.KEKRecipient;
import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.operator.OperatorException;
import org.mightyfish.operator.SymmetricKeyUnwrapper;
import org.mightyfish.operator.bc.BcSymmetricKeyUnwrapper;

public abstract class BcKEKRecipient
    implements KEKRecipient
{
    private SymmetricKeyUnwrapper unwrapper;

    public BcKEKRecipient(BcSymmetricKeyUnwrapper unwrapper)
    {
        this.unwrapper = unwrapper;
    }

    protected CipherParameters extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey)
        throws CMSException
    {
        try
        {
            return CMSUtils.getBcKey(unwrapper.generateUnwrappedKey(contentEncryptionAlgorithm, encryptedContentEncryptionKey));
        }
        catch (OperatorException e)
        {
            throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
        }
    }
}
