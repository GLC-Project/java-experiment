package org.mightyfish.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Mac;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.cms.CMSException;
import org.mightyfish.cms.RecipientOperator;
import org.mightyfish.jcajce.io.MacOutputStream;
import org.mightyfish.operator.GenericKey;
import org.mightyfish.operator.MacCalculator;
import org.mightyfish.operator.jcajce.JceGenericKey;

public class JcePasswordAuthenticatedRecipient
    extends JcePasswordRecipient
{
    public JcePasswordAuthenticatedRecipient(char[] password)
    {
        super(password);
    }

    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey)
        throws CMSException
    {
        final Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, derivedKey, encryptedContentEncryptionKey);

        final Mac dataMac = helper.createContentMac(secretKey, contentMacAlgorithm);

        return new RecipientOperator(new MacCalculator()
        {
            public AlgorithmIdentifier getAlgorithmIdentifier()
            {
                return contentMacAlgorithm;
            }

            public GenericKey getKey()
            {
                return new JceGenericKey(contentMacAlgorithm, secretKey);
            }

            public OutputStream getOutputStream()
            {
                return new MacOutputStream(dataMac);
            }

            public byte[] getMac()
            {
                return dataMac.doFinal();
            }
        });
    }
}
