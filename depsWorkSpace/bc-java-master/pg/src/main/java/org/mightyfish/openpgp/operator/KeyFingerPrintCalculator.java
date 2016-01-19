package org.mightyfish.openpgp.operator;

import org.mightyfish.bcpg.PublicKeyPacket;
import org.mightyfish.openpgp.PGPException;

public interface KeyFingerPrintCalculator
{
    byte[] calculateFingerprint(PublicKeyPacket publicPk)
        throws PGPException;
}
