package org.mightyfish.crypto.ec;

import org.mightyfish.crypto.CipherParameters;

public interface ECPairTransform
{
    void init(CipherParameters params);

    ECPair transform(ECPair cipherText);
}
