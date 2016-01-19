package org.mightyfish.crypto.generators;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.AsymmetricCipherKeyPairGenerator;
import org.mightyfish.crypto.EphemeralKeyPair;
import org.mightyfish.crypto.KeyEncoder;

public class EphemeralKeyPairGenerator
{
    private AsymmetricCipherKeyPairGenerator gen;
    private KeyEncoder keyEncoder;

    public EphemeralKeyPairGenerator(AsymmetricCipherKeyPairGenerator gen, KeyEncoder keyEncoder)
    {
        this.gen = gen;
        this.keyEncoder = keyEncoder;
    }

    public EphemeralKeyPair generate()
    {
        AsymmetricCipherKeyPair eph = gen.generateKeyPair();

        // Encode the ephemeral public key
         return new EphemeralKeyPair(eph, keyEncoder);
    }
}
