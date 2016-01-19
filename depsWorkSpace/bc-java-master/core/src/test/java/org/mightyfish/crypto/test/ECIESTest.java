package org.mightyfish.crypto.test;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.BufferedBlockCipher;
import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.crypto.KeyEncoder;
import org.mightyfish.crypto.KeyGenerationParameters;
import org.mightyfish.crypto.agreement.ECDHBasicAgreement;
import org.mightyfish.crypto.digests.SHA1Digest;
import org.mightyfish.crypto.engines.IESEngine;
import org.mightyfish.crypto.engines.TwofishEngine;
import org.mightyfish.crypto.generators.ECKeyPairGenerator;
import org.mightyfish.crypto.generators.EphemeralKeyPairGenerator;
import org.mightyfish.crypto.generators.KDF2BytesGenerator;
import org.mightyfish.crypto.macs.HMac;
import org.mightyfish.crypto.modes.CBCBlockCipher;
import org.mightyfish.crypto.paddings.PaddedBufferedBlockCipher;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.crypto.params.ECDomainParameters;
import org.mightyfish.crypto.params.ECKeyGenerationParameters;
import org.mightyfish.crypto.params.ECPrivateKeyParameters;
import org.mightyfish.crypto.params.ECPublicKeyParameters;
import org.mightyfish.crypto.params.IESParameters;
import org.mightyfish.crypto.params.IESWithCipherParameters;
import org.mightyfish.crypto.params.ParametersWithIV;
import org.mightyfish.crypto.parsers.ECIESPublicKeyParser;
import org.mightyfish.math.ec.ECConstants;
import org.mightyfish.math.ec.ECCurve;
import org.mightyfish.util.encoders.Hex;
import org.mightyfish.util.test.SimpleTest;

/**
 * test for ECIES - Elliptic Curve Integrated Encryption Scheme
 */
public class ECIESTest
    extends SimpleTest
{
    private static byte[] TWOFISH_IV = Hex.decode("000102030405060708090a0b0c0d0e0f");

    ECIESTest()
    {
    }

    public String getName()
    {
        return "ECIES";
    }

    private void doStaticTest(byte[] iv)
        throws Exception
    {
        BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");

        ECCurve.Fp curve = new ECCurve.Fp(
            new BigInteger("6277101735386680763835789423207666416083908700390324961279"), // q
            new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffc", 16), // a
            new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), // b
            n, ECConstants.ONE);

        ECDomainParameters params = new ECDomainParameters(
                curve,
                curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), // G
                n);

        ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(
            new BigInteger("651056770906015076056810763456358567190100156695615665659"), // d
            params);

        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(
            curve.decodePoint(Hex.decode("0262b12d60690cdcf330babab6e69763b471f994dd702d16a5")), // Q
            params);

        AsymmetricCipherKeyPair  p1 = new AsymmetricCipherKeyPair(pubKey, priKey);
        AsymmetricCipherKeyPair  p2 = new AsymmetricCipherKeyPair(pubKey, priKey);

        //
        // stream test
        //
        IESEngine i1 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));
        IESEngine i2 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));
        byte[]         d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[]         e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        CipherParameters p = new IESParameters(d, e, 64);

        i1.init(true, p1.getPrivate(), p2.getPublic(), p);
        i2.init(false, p2.getPrivate(), p1.getPublic(), p);

        byte[] message = Hex.decode("1234567890abcdef");

        byte[]   out1 = i1.processBlock(message, 0, message.length);

        if (!areEqual(out1, Hex.decode("468d89877e8238802403ec4cb6b329faeccfa6f3a730f2cdb3c0a8e8")))
        {
            fail("stream cipher test failed on enc");
        }

        byte[]   out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("stream cipher test failed");
        }

        //
        // twofish with CBC
        //
        BufferedBlockCipher c1 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        BufferedBlockCipher c2 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        i1 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c1);
        i2 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c2);
        d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        p = new IESWithCipherParameters(d, e, 64, 128);

        if (iv != null)
        {
            p = new ParametersWithIV(p, iv);
        }

        i1.init(true, p1.getPrivate(), p2.getPublic(), p);
        i2.init(false, p2.getPrivate(), p1.getPublic(), p);

        message = Hex.decode("1234567890abcdef");

        out1 = i1.processBlock(message, 0, message.length);

        if (!areEqual(out1, (iv == null) ?
                                  Hex.decode("b8a06ea5c2b9df28b58a0a90a734cde8c9c02903e5c220021fe4417410d1e53a32a71696")
                                : Hex.decode("f246b0e26a2711992cac9c590d08e45c5e730b7c0f4218bb064e27b7dd7c8a3bd8bf01c3")))
        {
            fail("twofish cipher test failed on enc");
        }

        out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("twofish cipher test failed");
        }
    }

    private void doEphemeralTest(byte[] iv)
        throws Exception
    {
        BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");

        ECCurve.Fp curve = new ECCurve.Fp(
            new BigInteger("6277101735386680763835789423207666416083908700390324961279"), // q
            new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffc", 16), // a
            new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), // b
            n, ECConstants.ONE);

        ECDomainParameters params = new ECDomainParameters(
                curve,
                curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), // G
                n);

        ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(
            new BigInteger("651056770906015076056810763456358567190100156695615665659"), // d
            params);

        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(
            curve.decodePoint(Hex.decode("0262b12d60690cdcf330babab6e69763b471f994dd702d16a5")), // Q
            params);

        AsymmetricCipherKeyPair  p1 = new AsymmetricCipherKeyPair(pubKey, priKey);
        AsymmetricCipherKeyPair  p2 = new AsymmetricCipherKeyPair(pubKey, priKey);

        // Generate the ephemeral key pair
        ECKeyPairGenerator gen = new ECKeyPairGenerator();
        gen.init(new ECKeyGenerationParameters(params, new SecureRandom()));

        EphemeralKeyPairGenerator ephKeyGen = new EphemeralKeyPairGenerator(gen, new KeyEncoder()
        {
            public byte[] getEncoded(AsymmetricKeyParameter keyParameter)
            {
                return ((ECPublicKeyParameters)keyParameter).getQ().getEncoded();
            }
        });

        //
        // stream test
        //
        IESEngine i1 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));
        IESEngine i2 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));

        byte[]            d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[]            e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        CipherParameters  p = new IESParameters(d, e, 64);

        i1.init(p2.getPublic(), p, ephKeyGen);
        i2.init(p2.getPrivate(), p, new ECIESPublicKeyParser(params));

        byte[] message = Hex.decode("1234567890abcdef");

        byte[]   out1 = i1.processBlock(message, 0, message.length);

        byte[]   out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("stream cipher test failed");
        }

        //
        // twofish with CBC
        //
        BufferedBlockCipher c1 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        BufferedBlockCipher c2 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        i1 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c1);
        i2 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c2);
        d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        p = new IESWithCipherParameters(d, e, 64, 128);

        if (iv != null)
        {
            p = new ParametersWithIV(p, iv);
        }

        i1.init(p2.getPublic(), p, ephKeyGen);
        i2.init(p2.getPrivate(), p, new ECIESPublicKeyParser(params));

        message = Hex.decode("1234567890abcdef");

        out1 = i1.processBlock(message, 0, message.length);

        out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("twofish cipher test failed");
        }
    }

    private void doTest(AsymmetricCipherKeyPair p1, AsymmetricCipherKeyPair p2)
        throws Exception
    {
        //
        // stream test
        //
        IESEngine i1 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));
        IESEngine i2 = new IESEngine(
                                   new ECDHBasicAgreement(),
                                   new KDF2BytesGenerator(new SHA1Digest()),
                                   new HMac(new SHA1Digest()));
        byte[]         d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        byte[]         e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        IESParameters  p = new IESParameters(d, e, 64);

        i1.init(true, p1.getPrivate(), p2.getPublic(), p);
        i2.init(false, p2.getPrivate(), p1.getPublic(), p);

        byte[] message = Hex.decode("1234567890abcdef");

        byte[]   out1 = i1.processBlock(message, 0, message.length);
 
        byte[]   out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("stream cipher test failed");
        }

        //
        // twofish with CBC
        //
        BufferedBlockCipher c1 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        BufferedBlockCipher c2 = new PaddedBufferedBlockCipher(
                                    new CBCBlockCipher(new TwofishEngine()));
        i1 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c1);
        i2 = new IESEngine(
                       new ECDHBasicAgreement(),
                       new KDF2BytesGenerator(new SHA1Digest()),
                       new HMac(new SHA1Digest()),
                       c2);
        d = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        e = new byte[] { 8, 7, 6, 5, 4, 3, 2, 1 };
        p = new IESWithCipherParameters(d, e, 64, 128);

        i1.init(true, p1.getPrivate(), p2.getPublic(), p);
        i2.init(false, p2.getPrivate(), p1.getPublic(), p);

        message = Hex.decode("1234567890abcdef");

        out1 = i1.processBlock(message, 0, message.length);

        out2 = i2.processBlock(out1, 0, out1.length);

        if (!areEqual(out2, message))
        {
            fail("twofish cipher test failed");
        }
    }

    public void performTest()
        throws Exception
    {
        doStaticTest(null);
        doStaticTest(TWOFISH_IV);

        BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");

        ECCurve.Fp curve = new ECCurve.Fp(
            new BigInteger("6277101735386680763835789423207666416083908700390324961279"), // q
            new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffc", 16), // a
            new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16), // b
            n, ECConstants.ONE);

        ECDomainParameters params = new ECDomainParameters(
                curve,
                curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), // G
                n);

        ECKeyPairGenerator eGen = new ECKeyPairGenerator();
        KeyGenerationParameters gParam = new ECKeyGenerationParameters(params, new SecureRandom());

        eGen.init(gParam);

        AsymmetricCipherKeyPair p1 = eGen.generateKeyPair();
        AsymmetricCipherKeyPair p2 = eGen.generateKeyPair();

        doTest(p1, p2);

        doEphemeralTest(null);
        doEphemeralTest(TWOFISH_IV);
    }

    public static void main(
        String[]    args)
    {
        runTest(new ECIESTest());
    }
}
