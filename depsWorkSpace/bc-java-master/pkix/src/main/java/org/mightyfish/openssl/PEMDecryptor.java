package org.mightyfish.openssl;

public interface PEMDecryptor
{
    byte[] decrypt(byte[] keyBytes, byte[] iv)
        throws PEMException;
}
