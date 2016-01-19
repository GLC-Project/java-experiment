package org.mightyfish.jcajce.provider.symmetric.util;

import org.mightyfish.crypto.BlockCipher;

public interface BlockCipherProvider
{
    BlockCipher get();
}
