package org.mightyfish.crypto.generators;

import java.math.BigInteger;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.AsymmetricCipherKeyPairGenerator;
import org.mightyfish.crypto.KeyGenerationParameters;
import org.mightyfish.crypto.params.DHParameters;
import org.mightyfish.crypto.params.ElGamalKeyGenerationParameters;
import org.mightyfish.crypto.params.ElGamalParameters;
import org.mightyfish.crypto.params.ElGamalPrivateKeyParameters;
import org.mightyfish.crypto.params.ElGamalPublicKeyParameters;

/**
 * a ElGamal key pair generator.
 * <p>
 * This generates keys consistent for use with ElGamal as described in
 * page 164 of "Handbook of Applied Cryptography".
 */
public class ElGamalKeyPairGenerator
    implements AsymmetricCipherKeyPairGenerator
{
    private ElGamalKeyGenerationParameters param;

    public void init(
        KeyGenerationParameters param)
    {
        this.param = (ElGamalKeyGenerationParameters)param;
    }

    public AsymmetricCipherKeyPair generateKeyPair()
    {
        DHKeyGeneratorHelper helper = DHKeyGeneratorHelper.INSTANCE;
        ElGamalParameters egp = param.getParameters();
        DHParameters dhp = new DHParameters(egp.getP(), egp.getG(), null, egp.getL());  

        BigInteger x = helper.calculatePrivate(dhp, param.getRandom()); 
        BigInteger y = helper.calculatePublic(dhp, x);

        return new AsymmetricCipherKeyPair(
            new ElGamalPublicKeyParameters(y, egp),
            new ElGamalPrivateKeyParameters(x, egp));
    }
}
