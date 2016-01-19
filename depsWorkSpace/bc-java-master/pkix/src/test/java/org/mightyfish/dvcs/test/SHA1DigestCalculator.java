package org.mightyfish.dvcs.test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.mightyfish.asn1.oiw.OIWObjectIdentifiers;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.digests.SHA1Digest;
import org.mightyfish.operator.DigestCalculator;


class SHA1DigestCalculator
    implements DigestCalculator
{
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    public AlgorithmIdentifier getAlgorithmIdentifier()
    {
        return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
    }

    public OutputStream getOutputStream()
    {
        return bOut;
    }

    public byte[] getDigest()
    {
        byte[] bytes = bOut.toByteArray();

        bOut.reset();

        Digest sha1 = new SHA1Digest();

        sha1.update(bytes, 0, bytes.length);

        byte[] digest = new byte[sha1.getDigestSize()];

        sha1.doFinal(digest, 0);

        return digest;
    }
}
