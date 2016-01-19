package org.mightyfish.cms.jcajce;

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.jcajce.util.NamedJcaJceHelper;
import org.mightyfish.operator.SymmetricKeyUnwrapper;
import org.mightyfish.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.mightyfish.operator.jcajce.JceSymmetricKeyUnwrapper;

class NamedJcaJceExtHelper
    extends NamedJcaJceHelper
    implements JcaJceExtHelper
{
    public NamedJcaJceExtHelper(String providerName)
    {
        super(providerName);
    }

    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey)
    {
        return new JceAsymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(providerName);
    }

    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, SecretKey keyEncryptionKey)
    {
        return new JceSymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(providerName);
    }
}