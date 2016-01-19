package org.mightyfish.openpgp.bc;

import java.io.IOException;
import java.io.InputStream;

import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPSecretKeyRing;
import org.mightyfish.openpgp.operator.KeyFingerPrintCalculator;
import org.mightyfish.openpgp.operator.bc.BcKeyFingerprintCalculator;

public class BcPGPSecretKeyRing
    extends PGPSecretKeyRing
{
    private static KeyFingerPrintCalculator fingerPrintCalculator = new BcKeyFingerprintCalculator();

    public BcPGPSecretKeyRing(byte[] encoding)
        throws IOException, PGPException
    {
        super(encoding, fingerPrintCalculator);
    }

    public BcPGPSecretKeyRing(InputStream in)
        throws IOException, PGPException
    {
        super(in, fingerPrintCalculator);
    }
}
