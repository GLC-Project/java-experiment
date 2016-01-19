package org.mightyfish.crypto.ec;

import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.math.ec.ECPoint;

public interface ECEncryptor
{
    void init(CipherParameters params);

    ECPair encrypt(ECPoint point);
}
