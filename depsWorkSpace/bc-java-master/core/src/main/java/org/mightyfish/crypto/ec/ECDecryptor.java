package org.mightyfish.crypto.ec;

import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.math.ec.ECPoint;

public interface ECDecryptor
{
    void init(CipherParameters params);

    ECPoint decrypt(ECPair cipherText);
}
