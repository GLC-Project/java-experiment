package org.mightyfish.crypto.modes.gcm;

public interface GCMExponentiator
{
    void init(byte[] x);
    void exponentiateX(long pow, byte[] output);
}
