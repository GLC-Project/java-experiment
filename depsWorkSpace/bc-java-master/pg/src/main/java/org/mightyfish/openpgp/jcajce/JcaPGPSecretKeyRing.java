package org.mightyfish.openpgp.jcajce;

import java.io.IOException;
import java.io.InputStream;

import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPSecretKeyRing;
import org.mightyfish.openpgp.operator.KeyFingerPrintCalculator;
import org.mightyfish.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

public class JcaPGPSecretKeyRing
    extends PGPSecretKeyRing
{
    private static KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();

    public JcaPGPSecretKeyRing(byte[] encoding)
        throws IOException, PGPException
    {
        super(encoding, fingerPrintCalculator);
    }

    public JcaPGPSecretKeyRing(InputStream in)
        throws IOException, PGPException
    {
        super(in, fingerPrintCalculator);
    }
}
