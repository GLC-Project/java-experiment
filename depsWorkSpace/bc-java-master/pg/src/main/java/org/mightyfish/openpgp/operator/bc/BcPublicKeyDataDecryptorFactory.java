package org.mightyfish.openpgp.operator.bc;

import org.mightyfish.asn1.nist.NISTNamedCurves;
import org.mightyfish.asn1.x9.X9ECParameters;
import org.mightyfish.bcpg.ECDHPublicBCPGKey;
import org.mightyfish.bcpg.ECSecretBCPGKey;
import org.mightyfish.crypto.AsymmetricBlockCipher;
import org.mightyfish.crypto.BlockCipher;
import org.mightyfish.crypto.BufferedAsymmetricBlockCipher;
import org.mightyfish.crypto.InvalidCipherTextException;
import org.mightyfish.crypto.Wrapper;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.crypto.params.ElGamalPrivateKeyParameters;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.math.ec.ECPoint;
import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPPrivateKey;
import org.mightyfish.openpgp.PGPPublicKey;
import org.mightyfish.openpgp.operator.PGPDataDecryptor;
import org.mightyfish.openpgp.operator.PGPPad;
import org.mightyfish.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.mightyfish.openpgp.operator.RFC6637KDFCalculator;

/**
 * A decryptor factory for handling public key decryption operations.
 */
public class BcPublicKeyDataDecryptorFactory
    implements PublicKeyDataDecryptorFactory
{
    private BcPGPKeyConverter keyConverter = new BcPGPKeyConverter();
    private PGPPrivateKey privKey;

    public BcPublicKeyDataDecryptorFactory(PGPPrivateKey privKey)
    {
        this.privKey = privKey;
    }

    public byte[] recoverSessionData(int keyAlgorithm, byte[][] secKeyData)
        throws PGPException
    {
        try
        {
            if (keyAlgorithm != PGPPublicKey.ECDH)
            {
                AsymmetricBlockCipher c = BcImplProvider.createPublicKeyCipher(keyAlgorithm);

                AsymmetricKeyParameter key = keyConverter.getPrivateKey(privKey);

                BufferedAsymmetricBlockCipher c1 = new BufferedAsymmetricBlockCipher(c);

                c1.init(false, key);

                if (keyAlgorithm == PGPPublicKey.RSA_ENCRYPT
                    || keyAlgorithm == PGPPublicKey.RSA_GENERAL)
                {
                    byte[] bi = secKeyData[0];

                    c1.processBytes(bi, 2, bi.length - 2);
                }
                else
                {
                    BcPGPKeyConverter converter = new BcPGPKeyConverter();
                    ElGamalPrivateKeyParameters parms = (ElGamalPrivateKeyParameters)converter.getPrivateKey(privKey);
                    int size = (parms.getParameters().getP().bitLength() + 7) / 8;
                    byte[] tmp = new byte[size];

                    byte[] bi = secKeyData[0]; // encoded MPI
                    if (bi.length - 2 > size)  // leading Zero? Shouldn't happen but...
                    {
                        c1.processBytes(bi, 3, bi.length - 3);
                    }
                    else
                    {
                        System.arraycopy(bi, 2, tmp, tmp.length - (bi.length - 2), bi.length - 2);
                        c1.processBytes(tmp, 0, tmp.length);
                    }

                    bi = secKeyData[1];  // encoded MPI
                    for (int i = 0; i != tmp.length; i++)
                    {
                        tmp[i] = 0;
                    }

                    if (bi.length - 2 > size) // leading Zero? Shouldn't happen but...
                    {
                        c1.processBytes(bi, 3, bi.length - 3);
                    }
                    else
                    {
                        System.arraycopy(bi, 2, tmp, tmp.length - (bi.length - 2), bi.length - 2);
                        c1.processBytes(tmp, 0, tmp.length);
                    }
                }

                return c1.doFinal();
            }
            else
            {
                ECDHPublicBCPGKey ecKey = (ECDHPublicBCPGKey)privKey.getPublicKeyPacket().getKey();
                X9ECParameters x9Params = NISTNamedCurves.getByOID(ecKey.getCurveOID());

                byte[] enc = secKeyData[0];

                int pLen = ((((enc[0] & 0xff) << 8) + (enc[1] & 0xff)) + 7) / 8;
                byte[] pEnc = new byte[pLen];

                System.arraycopy(enc, 2, pEnc, 0, pLen);

                byte[] keyEnc = new byte[enc[pLen + 2]];

                System.arraycopy(enc, 2 + pLen + 1, keyEnc, 0, keyEnc.length);

                Wrapper c = BcImplProvider.createWrapper(ecKey.getSymmetricKeyAlgorithm());

                ECPoint S = x9Params.getCurve().decodePoint(pEnc).multiply(((ECSecretBCPGKey)privKey.getPrivateKeyDataPacket()).getX()).normalize();

                RFC6637KDFCalculator rfc6637KDFCalculator = new RFC6637KDFCalculator(new BcPGPDigestCalculatorProvider().get(ecKey.getHashAlgorithm()), ecKey.getSymmetricKeyAlgorithm());
                KeyParameter key = new KeyParameter(rfc6637KDFCalculator.createKey(ecKey.getCurveOID(), S, new BcKeyFingerprintCalculator().calculateFingerprint(privKey.getPublicKeyPacket())));

                c.init(false, key);

                return PGPPad.unpadSessionData(c.unwrap(keyEnc, 0, keyEnc.length));
            }
        }
        catch (InvalidCipherTextException e)
        {
            throw new PGPException("exception encrypting session info: " + e.getMessage(), e);
        }

    }

    public PGPDataDecryptor createDataDecryptor(boolean withIntegrityPacket, int encAlgorithm, byte[] key)
        throws PGPException
    {
        BlockCipher engine = BcImplProvider.createBlockCipher(encAlgorithm);

        return BcUtil.createDataDecryptor(withIntegrityPacket, engine, key);
    }
}
