package org.mightyfish.cms.jcajce;

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.jcajce.util.JcaJceHelper;
import org.mightyfish.operator.SymmetricKeyUnwrapper;
import org.mightyfish.operator.jcajce.JceAsymmetricKeyUnwrapper;

public interface JcaJceExtHelper
    extends JcaJceHelper
{
    JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey);

    SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, SecretKey keyEncryptionKey);
}
