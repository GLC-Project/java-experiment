package org.mightyfish.crypto.prng;

import org.mightyfish.crypto.prng.drbg.SP80090DRBG;

interface DRBGProvider
{
    SP80090DRBG get(EntropySource entropySource);
}
