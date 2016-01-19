package org.mightyfish.crypto;

import java.io.IOException;
import java.io.InputStream;

import org.mightyfish.crypto.params.AsymmetricKeyParameter;

public interface KeyParser
{
    AsymmetricKeyParameter readKey(InputStream stream)
        throws IOException;
}
