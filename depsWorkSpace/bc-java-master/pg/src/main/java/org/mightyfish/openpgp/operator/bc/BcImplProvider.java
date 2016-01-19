package org.mightyfish.openpgp.operator.bc;

import org.mightyfish.bcpg.HashAlgorithmTags;
import org.mightyfish.bcpg.PublicKeyAlgorithmTags;
import org.mightyfish.bcpg.SymmetricKeyAlgorithmTags;
import org.mightyfish.crypto.AsymmetricBlockCipher;
import org.mightyfish.crypto.BlockCipher;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.Signer;
import org.mightyfish.crypto.Wrapper;
import org.mightyfish.crypto.digests.MD2Digest;
import org.mightyfish.crypto.digests.MD5Digest;
import org.mightyfish.crypto.digests.RIPEMD160Digest;
import org.mightyfish.crypto.digests.SHA1Digest;
import org.mightyfish.crypto.digests.SHA224Digest;
import org.mightyfish.crypto.digests.SHA256Digest;
import org.mightyfish.crypto.digests.SHA384Digest;
import org.mightyfish.crypto.digests.SHA512Digest;
import org.mightyfish.crypto.digests.TigerDigest;
import org.mightyfish.crypto.encodings.PKCS1Encoding;
import org.mightyfish.crypto.engines.AESEngine;
import org.mightyfish.crypto.engines.AESFastEngine;
import org.mightyfish.crypto.engines.BlowfishEngine;
import org.mightyfish.crypto.engines.CAST5Engine;
import org.mightyfish.crypto.engines.CamelliaEngine;
import org.mightyfish.crypto.engines.DESEngine;
import org.mightyfish.crypto.engines.DESedeEngine;
import org.mightyfish.crypto.engines.ElGamalEngine;
import org.mightyfish.crypto.engines.IDEAEngine;
import org.mightyfish.crypto.engines.RFC3394WrapEngine;
import org.mightyfish.crypto.engines.RSABlindedEngine;
import org.mightyfish.crypto.engines.TwofishEngine;
import org.mightyfish.crypto.signers.DSADigestSigner;
import org.mightyfish.crypto.signers.DSASigner;
import org.mightyfish.crypto.signers.ECDSASigner;
import org.mightyfish.crypto.signers.RSADigestSigner;
import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPPublicKey;

class BcImplProvider
{
    static Digest createDigest(int algorithm)
        throws PGPException
    {
        switch (algorithm)
        {
        case HashAlgorithmTags.SHA1:
            return new SHA1Digest();
        case HashAlgorithmTags.SHA224:
            return new SHA224Digest();
        case HashAlgorithmTags.SHA256:
            return new SHA256Digest();
        case HashAlgorithmTags.SHA384:
            return new SHA384Digest();
        case HashAlgorithmTags.SHA512:
            return new SHA512Digest();
        case HashAlgorithmTags.MD2:
            return new MD2Digest();
        case HashAlgorithmTags.MD5:
            return new MD5Digest();
        case HashAlgorithmTags.RIPEMD160:
            return new RIPEMD160Digest();
        case HashAlgorithmTags.TIGER_192:
            return new TigerDigest();
        default:
            throw new PGPException("cannot recognise digest");
        }
    }

    static Signer createSigner(int keyAlgorithm, int hashAlgorithm)
        throws PGPException
    {
        switch(keyAlgorithm)
        {
        case PublicKeyAlgorithmTags.RSA_GENERAL:
        case PublicKeyAlgorithmTags.RSA_SIGN:
            return new RSADigestSigner(createDigest(hashAlgorithm));
        case PublicKeyAlgorithmTags.DSA:
            return new DSADigestSigner(new DSASigner(), createDigest(hashAlgorithm));
        case PublicKeyAlgorithmTags.ECDSA:
            return new DSADigestSigner(new ECDSASigner(), createDigest(hashAlgorithm));
        default:
            throw new PGPException("cannot recognise keyAlgorithm: " + keyAlgorithm);
        }
    }

    static BlockCipher createBlockCipher(int encAlgorithm)
        throws PGPException
    {
        BlockCipher engine;

        switch (encAlgorithm)
        {
        case SymmetricKeyAlgorithmTags.AES_128:
        case SymmetricKeyAlgorithmTags.AES_192:
        case SymmetricKeyAlgorithmTags.AES_256:
            engine = new AESEngine();
            break;
        case SymmetricKeyAlgorithmTags.CAMELLIA_128:
        case SymmetricKeyAlgorithmTags.CAMELLIA_192:
        case SymmetricKeyAlgorithmTags.CAMELLIA_256:
            engine = new CamelliaEngine();
            break;
        case SymmetricKeyAlgorithmTags.BLOWFISH:
            engine = new BlowfishEngine();
            break;
        case SymmetricKeyAlgorithmTags.CAST5:
            engine = new CAST5Engine();
            break;
        case SymmetricKeyAlgorithmTags.DES:
            engine = new DESEngine();
            break;
        case SymmetricKeyAlgorithmTags.IDEA:
            engine = new IDEAEngine();
            break;
        case SymmetricKeyAlgorithmTags.TWOFISH:
            engine = new TwofishEngine();
            break;
        case SymmetricKeyAlgorithmTags.TRIPLE_DES:
            engine = new DESedeEngine();
            break;
        default:
            throw new PGPException("cannot recognise cipher");
        }

        return engine;
    }

    static Wrapper createWrapper(int encAlgorithm)
        throws PGPException
    {
        switch (encAlgorithm)
        {
        case SymmetricKeyAlgorithmTags.AES_128:
        case SymmetricKeyAlgorithmTags.AES_192:
        case SymmetricKeyAlgorithmTags.AES_256:
            return new RFC3394WrapEngine(new AESFastEngine());
        case SymmetricKeyAlgorithmTags.CAMELLIA_128:
        case SymmetricKeyAlgorithmTags.CAMELLIA_192:
        case SymmetricKeyAlgorithmTags.CAMELLIA_256:
            return new RFC3394WrapEngine(new CamelliaEngine());
        default:
            throw new PGPException("unknown wrap algorithm: " + encAlgorithm);
        }
    }

    static AsymmetricBlockCipher createPublicKeyCipher(int encAlgorithm)
        throws PGPException
    {
        AsymmetricBlockCipher c;

        switch (encAlgorithm)
        {
        case PGPPublicKey.RSA_ENCRYPT:
        case PGPPublicKey.RSA_GENERAL:
            c = new PKCS1Encoding(new RSABlindedEngine());
            break;
        case PGPPublicKey.ELGAMAL_ENCRYPT:
        case PGPPublicKey.ELGAMAL_GENERAL:
            c = new PKCS1Encoding(new ElGamalEngine());
            break;
        case PGPPublicKey.DSA:
            throw new PGPException("Can't use DSA for encryption.");
        case PGPPublicKey.ECDSA:
            throw new PGPException("Can't use ECDSA for encryption.");
        case PGPPublicKey.ECDH:
            throw new PGPException("Not implemented.");
        default:
            throw new PGPException("unknown asymmetric algorithm: " + encAlgorithm);
        }

        return c;
    }
}
