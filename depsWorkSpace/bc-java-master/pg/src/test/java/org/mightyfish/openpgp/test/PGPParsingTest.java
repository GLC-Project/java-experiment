package org.mightyfish.openpgp.test;

import java.security.Security;

import org.mightyfish.jce.provider.BouncyCastleProvider;
import org.mightyfish.openpgp.PGPPublicKeyRingCollection;
import org.mightyfish.openpgp.PGPUtil;
import org.mightyfish.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.mightyfish.util.test.SimpleTest;

public class PGPParsingTest
    extends SimpleTest
{
    public void performTest()
        throws Exception
    {
        PGPPublicKeyRingCollection pubRingCollection = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(this.getClass().getResourceAsStream("bigpub.asc")), new JcaKeyFingerprintCalculator());
    }

    public String getName()
    {
        return "PGPParsingTest";
    }

    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        runTest(new PGPParsingTest());
    }
}
