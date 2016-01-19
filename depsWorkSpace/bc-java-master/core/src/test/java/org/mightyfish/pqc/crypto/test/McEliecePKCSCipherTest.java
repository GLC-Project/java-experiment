package org.mightyfish.pqc.crypto.test;

import java.security.SecureRandom;
import java.util.Random;

import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.digests.SHA256Digest;
import org.mightyfish.crypto.params.ParametersWithRandom;
import org.mightyfish.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.mightyfish.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.mightyfish.pqc.crypto.mceliece.McEliecePKCSCipher;
import org.mightyfish.pqc.crypto.mceliece.McEliecePKCSDigestCipher;
import org.mightyfish.pqc.crypto.mceliece.McElieceParameters;
import org.mightyfish.util.test.SimpleTest;

public class McEliecePKCSCipherTest
    extends SimpleTest
{

    SecureRandom keyRandom = new SecureRandom();

    public String getName()
    {
        return "McEliecePKCS";

    }


    public void performTest()
    {
        int numPassesKPG = 1;
        int numPassesEncDec = 10;
        Random rand = new Random();
        byte[] mBytes;
        for (int j = 0; j < numPassesKPG; j++)
        {

            McElieceParameters params = new McElieceParameters();
            McElieceKeyPairGenerator mcElieceKeyGen = new McElieceKeyPairGenerator();
            McElieceKeyGenerationParameters genParam = new McElieceKeyGenerationParameters(keyRandom, params);

            mcElieceKeyGen.init(genParam);
            AsymmetricCipherKeyPair pair = mcElieceKeyGen.generateKeyPair();

            ParametersWithRandom param = new ParametersWithRandom(pair.getPublic(), keyRandom);
            Digest msgDigest = new SHA256Digest();
            McEliecePKCSDigestCipher mcEliecePKCSDigestCipher = new McEliecePKCSDigestCipher(new McEliecePKCSCipher(), msgDigest);


            for (int k = 1; k <= numPassesEncDec; k++)
            {
                System.out.println("############### test: " + k);
                // initialize for encryption
                mcEliecePKCSDigestCipher.init(true, param);

                // generate random message
                int mLength = (rand.nextInt() & 0x1f) + 1;
                mBytes = new byte[mLength];
                rand.nextBytes(mBytes);

                // encrypt
                mcEliecePKCSDigestCipher.update(mBytes, 0, mBytes.length);
                byte[] enc = mcEliecePKCSDigestCipher.messageEncrypt();

                // initialize for decryption
                mcEliecePKCSDigestCipher.init(false, pair.getPrivate());
                byte[] constructedmessage = mcEliecePKCSDigestCipher.messageDecrypt(enc);

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
        runTest(new McEliecePKCSCipherTest());
    }

}
