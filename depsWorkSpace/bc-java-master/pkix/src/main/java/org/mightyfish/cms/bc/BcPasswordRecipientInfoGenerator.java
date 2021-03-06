package org.mightyfish.cms.bc;

import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.ASN1OctetString;
import org.mightyfish.asn1.pkcs.PBKDF2Params;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.cms.CMSException;
import org.mightyfish.cms.PasswordRecipientInfoGenerator;
import org.mightyfish.crypto.Wrapper;
import org.mightyfish.crypto.generators.PKCS5S2ParametersGenerator;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.crypto.params.ParametersWithIV;
import org.mightyfish.operator.GenericKey;

public class BcPasswordRecipientInfoGenerator
    extends PasswordRecipientInfoGenerator
{
    public BcPasswordRecipientInfoGenerator(ASN1ObjectIdentifier kekAlgorithm, char[] password)
    {
        super(kekAlgorithm, password);
    }

    protected byte[] calculateDerivedKey(byte[] encodedPassword, AlgorithmIdentifier derivationAlgorithm, int keySize)
        throws CMSException
    {
        PBKDF2Params params = PBKDF2Params.getInstance(derivationAlgorithm.getParameters());

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator();

        gen.init(encodedPassword, params.getSalt(), params.getIterationCount().intValue());

        return ((KeyParameter)gen.generateDerivedParameters(keySize)).getKey();
    }

    public byte[] generateEncryptedBytes(AlgorithmIdentifier keyEncryptionAlgorithm, byte[] derivedKey, GenericKey contentEncryptionKey)
        throws CMSException
    {
        byte[] contentEncryptionKeySpec = ((KeyParameter)CMSUtils.getBcKey(contentEncryptionKey)).getKey();
        Wrapper keyEncryptionCipher = EnvelopedDataHelper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());

        keyEncryptionCipher.init(true, new ParametersWithIV(new KeyParameter(derivedKey), ASN1OctetString.getInstance(keyEncryptionAlgorithm.getParameters()).getOctets()));

        return keyEncryptionCipher.wrap(contentEncryptionKeySpec, 0, contentEncryptionKeySpec.length);
    }
}
