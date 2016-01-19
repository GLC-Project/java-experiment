package org.mightyfish.pqc.crypto.test;

import java.security.SecureRandom;
import java.util.Random;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.digests.SHA256Digest;
import org.mightyfish.crypto.params.ParametersWithRandom;
import org.mightyfish.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.mightyfish.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.mightyfish.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.mightyfish.pqc.crypto.mceliece.McElieceKobaraImaiCipher;
import org.mightyfish.pqc.crypto.mceliece.McElieceKobaraImaiDigestCipher;
import org.mightyfish.util.test.SimpleTest;

public class McElieceKobaraImaiCipherTest
    extends SimpleTest
{

    SecureRandom keyRandom = new SecureRandom();

    public String getName()
    {
        return "McElieceKobaraImai";

    }


    public void performTest()
    {
        int numPassesKPG = 1;
        int numPassesEncDec = 10;
        Random rand = new Random();
        byte[] mBytes;
        for (int j = 0; j < numPassesKPG; j++)
        {

            McElieceCCA2Parameters params = new McElieceCCA2Parameters();
            McElieceCCA2KeyPairGenerator mcElieceCCA2KeyGen = new McElieceCCA2KeyPairGenerator();
            McElieceCCA2KeyGenerationParameters genParam = new McElieceCCA2KeyGenerationParameters(keyRandom, params);

            mcElieceCCA2KeyGen.init(genParam);
            AsymmetricCipherKeyPair pair = mcElieceCCA2KeyGen.generateKeyPair();

            ParametersWithRandom param = new ParametersWithRandom(pair.getPublic(), keyRandom);
            Digest msgDigest = new SHA256Digest();
            McElieceKobaraImaiDigestCipher mcElieceKobaraImaiDigestCipher = new McElieceKobaraImaiDigestCipher(new McElieceKobaraImaiCipher(), msgDigest);


            for (int k = 1; k <= numPassesEncDec; k++)
            {
                System.out.println("############### test: " + k);
                // initialize for encryption
                mcElieceKobaraImaiDigestCipher.init(true, param);

                // generate random message
                int mLength = (rand.nextInt() & 0x1f) + 1;
                mBytes = new byte[mLength];
                rand.nextBytes(mBytes);

                // encrypt
                mcElieceKobaraImaiDigestCipher.update(mBytes, 0, mBytes.length);
                byte[] enc = mcElieceKobaraImaiDigestCipher.messageEncrypt();

                // initialize for decryption
                mcElieceKobaraImaiDigestCipher.init(false, pair.getPrivate());
                byte[] constructedmessage = mcElieceKobaraImaiDigestCipher.messageDecrypt(enc);

                // XXX write in McElieceFujisakiDigestCipher?
                msgDigest.update(mBytes, 0, mBytes.length);
                byte[] hash = new byte[msgDigest.getDigestSize()];
                msgDigest.doFinal(hash, 0);

                boolean verified = true;
                for (int i = 0; i < hash.length; i++)
                {
                    verified = verified && hash[i] == constructedmessage[i];
                }

                if (!verified)
                {
                    fail("en/decryption fails");
                }
                else
                {
                    System.out.println("test okay");
                    System.out.println();
                }

            }
        }

    }

    public static void main(
        String[] args)
    {
        runTest(new McElieceKobaraImaiCipherTest());
    }

}
