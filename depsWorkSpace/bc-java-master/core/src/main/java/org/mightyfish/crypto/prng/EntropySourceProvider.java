package org.mightyfish.crypto.prng;

public interface EntropySourceProvider
{
    EntropySource get(final int bitsRequired);
}
