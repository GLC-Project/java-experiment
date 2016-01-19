package org.mightyfish.openpgp.jcajce;

import java.io.IOException;
import java.io.InputStream;

import org.mightyfish.openpgp.PGPPublicKeyRing;
import org.mightyfish.openpgp.operator.KeyFingerPrintCalculator;
import org.mightyfish.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

public class JcaPGPPublicKeyRing
    extends PGPPublicKeyRing
{
    private static KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();

    public JcaPGPPublicKeyRing(byte[] encoding)
        throws IOException
    {
        super(encoding, fingerPrintCalculator);
    }

    public JcaPGPPublicKeyRing(InputStream in)
        throws IOException
    {
        super(in, fingerPrintCalculator);
    }
}
