package org.mightyfish.openpgp.operator.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.mightyfish.asn1.nist.NISTNamedCurves;
import org.mightyfish.asn1.x9.X9ECParameters;
import org.mightyfish.bcpg.ECDHPublicBCPGKey;
import org.mightyfish.bcpg.MPInteger;
import org.mightyfish.crypto.AsymmetricBlockCipher;
import org.mightyfish.crypto.EphemeralKeyPair;
import org.mightyfish.crypto.InvalidCipherTextException;
import org.mightyfish.crypto.KeyEncoder;
import org.mightyfish.crypto.Wrapper;
import org.mightyfish.crypto.generators.ECKeyPairGenerator;
import org.mightyfish.crypto.generators.EphemeralKeyPairGenerator;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.crypto.params.ECDomainParameters;
import org.mightyfish.crypto.params.ECKeyGenerationParameters;
import org.mightyfish.crypto.params.ECPrivateKeyParameters;
import org.mightyfish.crypto.params.ECPublicKeyParameters;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.crypto.params.ParametersWithRandom;
import org.mightyfish.math.ec.ECPoint;
import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPPublicKey;
import org.mightyfish.openpgp.operator.PGPPad;
import org.mightyfish.openpgp.operator.PublicKeyKeyEncryptionMethodGenerator;
import org.mightyfish.openpgp.operator.RFC6637KDFCalculator;

/**
 * A method generator for supporting public key based encryption operations.
 */
public class BcPublicKeyKeyEncryptionMethodGenerator
    extends PublicKeyKeyEncryptionMethodGenerator
{
    private SecureRandom random;
    private BcPGPKeyConverter keyConverter = new BcPGPKeyConverter();

    /**
     * Create a public key encryption method generator with the method to be based on the passed in key.
     *
     * @param key   the public key to use for encryption.
     */
    public BcPublicKeyKeyEncryptionMethodGenerator(PGPPublicKey key)
    {
        super(key);
    }

    /**
     * Provide a user defined source of randomness.
     *
     * @param random  the secure random to be used.
     * @return  the current generator.
     */
    public BcPublicKeyKeyEncryptionMethodGenerator setSecureRandom(SecureRandom random)
    {
        this.random = random;

        return this;
    }

    protected byte[] encryptSessionInfo(PGPPublicKey pubKey, byte[] sessionInfo)
        throws PGPException
    {
        try
        {
            if (pubKey.getAlgorithm() != PGPPublicKey.ECDH)
            {
                AsymmetricBlockCipher c = BcImplProvider.createPublicKeyCipher(pubKey.getAlgorithm());

                AsymmetricKeyParameter key = keyConverter.getPublicKey(pubKey);

                if (random == null)
                {
                    random = new SecureRandom();
                }

                c.init(true, new ParametersWithRandom(key, random));

                return c.processBlock(sessionInfo, 0, sessionInfo.length);
            }
            else
            {
                ECDHPublicBCPGKey ecKey = (ECDHPublicBCPGKey)pubKey.getPublicKeyPacket().getKey();
                X9ECParameters x9Params = NISTNamedCurves.getByOID(ecKey.getCurveOID());
                ECDomainParameters ecParams = new ECDomainParameters(x9Params.getCurve(), x9Params.getG(), x9Params.getN());

                // Generate the ephemeral key pair
                ECKeyPairGenerator gen = new ECKeyPairGenerator();
                gen.init(new ECKeyGenerationParameters(ecParams, random));

                EphemeralKeyPairGenerator kGen = new EphemeralKeyPairGenerator(gen, new KeyEncoder()
                {
                    public byte[] getEncoded(AsymmetricKeyParameter keyParameter)
                    {
                        return ((ECPublicKeyParameters)keyParameter).getQ().getEncoded(false);
                    }
                });

                EphemeralKeyPair ephKp = kGen.generate();

                ECPrivateKeyParameters ephPriv = (ECPrivateKeyParameters)ephKp.getKeyPair().getPrivate();

                ECPoint S = ecKey.getPoint().multiply(ephPriv.getD()).normalize();

                RFC6637KDFCalculator rfc6637KDFCalculator = new RFC6637KDFCalculator(new BcPGPDigestCalculatorProvider().get(ecKey.getHashAlgorithm()), ecKey.getSymmetricKeyAlgorithm());

                KeyParameter key = new KeyParameter(rfc6637KDFCalculator.createKey(ecKey.getCurveOID(), S, pubKey.getFingerprint()));

                Wrapper c = BcImplProvider.createWrapper(ecKey.getSymmetricKeyAlgorithm());

                c.init(true, new ParametersWithRandom(key, random));

                byte[] paddedSessionData = PGPPad.padSessionData(sessionInfo);

                byte[] C = c.wrap(paddedSessionData, 0, paddedSessionData.length);
                byte[] VB = new MPInteger(new BigInteger(1, ephKp.getEncodedPublicKey())).getEncoded();

                byte[] rv = new byte[VB.length + 1 + C.length];

                System.arraycopy(VB, 0, rv, 0, VB.length);
                rv[VB.length] = (byte)C.length;
                System.arraycopy(C, 0, rv, VB.length + 1, C.length);

                return rv;
            }
        }
        catch (InvalidCipherTextException e)
        {
            throw new PGPException("exception encrypting session info: " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new PGPException("exception encrypting session info: " + e.getMessage(), e);
        }
    }
}
