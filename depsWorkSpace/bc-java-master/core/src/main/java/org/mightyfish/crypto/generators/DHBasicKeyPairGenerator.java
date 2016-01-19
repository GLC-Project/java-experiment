package org.mightyfish.crypto.generators;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.AsymmetricCipherKeyPairGenerator;
import org.mightyfish.crypto.KeyGenerationParameters;
import org.mightyfish.crypto.params.DHKeyGenerationParameters;
import org.mightyfish.crypto.params.DHParameters;
import org.mightyfish.crypto.params.DHPrivateKeyParameters;
import org.mightyfish.crypto.params.DHPublicKeyParameters;

import java.math.BigInteger;

/**
 * a basic Diffie-Hellman key pair generator.
 *
 * This generates keys consistent for use with the basic algorithm for
 * Diffie-Hellman.
 */
public class DHBasicKeyPairGenerator
    implements AsymmetricCipherKeyPairGenerator
{
    private DHKeyGenerationParameters param;

    public void init(
        KeyGenerationParameters param)
    {
        this.param = (DHKeyGenerationParameters)param;
    }

    public AsymmetricCipherKeyPair generateKeyPair()
    {
        DHKeyGeneratorHelper helper = DHKeyGeneratorHelper.INSTANCE;
        DHParameters dhp = param.getParameters();

        BigInteger x = helper.calculatePrivate(dhp, param.getRandom()); 
        BigInteger y = helper.calculatePublic(dhp, x);

        return new AsymmetricCipherKeyPair(
            new DHPublicKeyParameters(y, dhp),
            new DHPrivateKeyParameters(x, dhp));
    }
}
