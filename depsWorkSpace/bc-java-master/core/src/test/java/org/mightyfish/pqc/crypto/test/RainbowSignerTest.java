package org.mightyfish.pqc.crypto.test;


import java.math.BigInteger;
import java.security.SecureRandom;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.digests.SHA224Digest;
import org.mightyfish.crypto.params.ParametersWithRandom;
import org.mightyfish.pqc.crypto.DigestingMessageSigner;
import org.mightyfish.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.mightyfish.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.mightyfish.pqc.crypto.rainbow.RainbowParameters;
import org.mightyfish.pqc.crypto.rainbow.RainbowSigner;
import org.mightyfish.util.BigIntegers;
import org.mightyfish.util.encoders.Hex;
import org.mightyfish.util.test.FixedSecureRandom;
import org.mightyfish.util.test.SimpleTest;


public class RainbowSignerTest
extends SimpleTest
{
    byte[] keyData = Hex.decode("b5014e4b60ef2ba8b6211b4062ba3224e0427dd3");

    SecureRandom    keyRandom = new FixedSecureRandom(new byte[][] { keyData, keyData });


    public String getName()
    {
        return "Rainbow";
    }

    public void performTest()
    {
        RainbowParameters params = new RainbowParameters();

        RainbowKeyPairGenerator rainbowKeyGen = new RainbowKeyPairGenerator();
        RainbowKeyGenerationParameters genParam = new RainbowKeyGenerationParameters(keyRandom, params);

        rainbowKeyGen.init(genParam);

        AsymmetricCipherKeyPair pair = rainbowKeyGen.generateKeyPair();

        ParametersWithRandom param = new ParametersWithRandom(pair.getPrivate(), keyRandom);

        DigestingMessageSigner rainbowSigner = new DigestingMessageSigner(new RainbowSigner() , new SHA224Digest());
        rainbowSigner.init(true, param);

        byte[] message = BigIntegers.asUnsignedByteArray(new BigInteger("968236873715988614170569073515315707566766479517"));
        rainbowSigner.update(message, 0, message.length);
        byte[] sig = rainbowSigner.generateSignature();

        rainbowSigner.init(false, pair.getPublic());
        rainbowSigner.update(message, 0, message.length);
        if (!rainbowSigner.verify(sig))
        {
            fail("verification fails");
        }
    }

    public static void main(
            String[]    args)
    {
        runTest(new RainbowSignerTest());
    }
}
