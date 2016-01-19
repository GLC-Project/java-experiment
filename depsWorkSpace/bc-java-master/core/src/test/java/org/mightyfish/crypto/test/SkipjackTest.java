package org.mightyfish.crypto.test;

import org.mightyfish.crypto.engines.SkipjackEngine;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.util.encoders.Hex;
import org.mightyfish.util.test.SimpleTest;

/**
 */
public class SkipjackTest
    extends CipherTest
{
    static SimpleTest[]  tests = 
            {
                new BlockCipherVectorTest(0, new SkipjackEngine(),
                        new KeyParameter(Hex.decode("00998877665544332211")),
                        "33221100ddccbbaa", "2587cae27a12d300")
            };

    SkipjackTest()
    {
        super(tests, new SkipjackEngine(), new KeyParameter(Hex.decode("00998877665544332211")));
    }

    public String getName()
    {
        return "SKIPJACK";
    }

    public static void main(
        String[]    args)
    {
        runTest(new SkipjackTest());
    }
}
