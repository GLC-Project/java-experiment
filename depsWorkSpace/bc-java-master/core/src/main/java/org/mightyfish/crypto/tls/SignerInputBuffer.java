package org.mightyfish.crypto.tls;

import java.io.ByteArrayOutputStream;

import org.mightyfish.crypto.Signer;

class SignerInputBuffer extends ByteArrayOutputStream
{
    void updateSigner(Signer s)
    {
        s.update(this.buf, 0, count);
    }
}