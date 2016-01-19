package org.mightyfish.openpgp.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;
import java.util.Iterator;

import org.mightyfish.asn1.nist.NISTNamedCurves;
import org.mightyfish.asn1.x9.X9ECParameters;
import org.mightyfish.bcpg.HashAlgorithmTags;
import org.mightyfish.bcpg.SymmetricKeyAlgorithmTags;
import org.mightyfish.crypto.AsymmetricCipherKeyPair;
import org.mightyfish.crypto.generators.ECKeyPairGenerator;
import org.mightyfish.crypto.params.ECKeyGenerationParameters;
import org.mightyfish.crypto.params.ECNamedDomainParameters;
import org.mightyfish.jce.provider.BouncyCastleProvider;
import org.mightyfish.openpgp.PGPEncryptedData;
import org.mightyfish.openpgp.PGPEncryptedDataGenerator;
import org.mightyfish.openpgp.PGPEncryptedDataList;
import org.mightyfish.openpgp.PGPException;
import org.mightyfish.openpgp.PGPKeyPair;
import org.mightyfish.openpgp.PGPKeyRingGenerator;
import org.mightyfish.openpgp.PGPLiteralData;
import org.mightyfish.openpgp.PGPLiteralDataGenerator;
import org.mightyfish.openpgp.PGPPrivateKey;
import org.mightyfish.openpgp.PGPPublicKey;
import org.mightyfish.openpgp.PGPPublicKeyEncryptedData;
import org.mightyfish.openpgp.PGPPublicKeyRing;
import org.mightyfish.openpgp.PGPSecretKey;
import org.mightyfish.openpgp.PGPSecretKeyRing;
import org.mightyfish.openpgp.PGPSignature;
import org.mightyfish.openpgp.PGPUtil;
import org.mightyfish.openpgp.jcajce.JcaPGPObjectFactory;
import org.mightyfish.openpgp.operator.KeyFingerPrintCalculator;
import org.mightyfish.openpgp.operator.PGPDigestCalculator;
import org.mightyfish.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.mightyfish.openpgp.operator.bc.BcPGPKeyPair;
import org.mightyfish.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.mightyfish.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.mightyfish.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.mightyfish.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Base64;
import org.mightyfish.util.test.SimpleTest;
import org.mightyfish.util.test.UncloseableOutputStream;

public class PGPECDHTest
    extends SimpleTest
{
    byte[] testPubKey =
        Base64.decode(
            "mFIEUb4GwBMIKoZIzj0DAQcCAwS8p3TFaRAx58qCG63W+UNthXBPSJDnVDPTb/sT" +
            "iXePaAZ/Gh1GKXTq7k6ab/67MMeVFp/EdySumqdWLtvceFKstFBUZXN0IEVDRFNB" +
            "LUVDREggKEtleSBhbmQgc3Via2V5IGFyZSAyNTYgYml0cyBsb25nKSA8dGVzdC5l" +
            "Y2RzYS5lY2RoQGV4YW1wbGUuY29tPoh6BBMTCAAiBQJRvgbAAhsDBgsJCAcDAgYV" +
            "CAIJCgsEFgIDAQIeAQIXgAAKCRD3wDlWjFo9U5O2AQDi89NO6JbaIObC63jMMWsi" +
            "AaQHrBCPkDZLibgNv73DLgD/faouH4YZJs+cONQBPVnP1baG1NpWR5ppN3JULFcr" +
            "hcq4VgRRvgbAEggqhkjOPQMBBwIDBLtY8Nmfz0zSEa8C1snTOWN+VcT8pXPwgJRy" +
            "z6kSP4nPt1xj1lPKj5zwPXKWxMkPO9ocqhKdg2mOh6/rc1ObIoMDAQgHiGEEGBMI" +
            "AAkFAlG+BsACGwwACgkQ98A5VoxaPVN8cgEAj4dMNMNwRSg2ZBWunqUAHqIedVbS" +
            "dmwmbysD192L3z4A/ReXEa0gtv8OFWjuALD1ovEK8TpDORLUb6IuUb5jUIzY");

    byte[] testPrivKey =
        Base64.decode(
            "lKUEUb4GwBMIKoZIzj0DAQcCAwS8p3TFaRAx58qCG63W+UNthXBPSJDnVDPTb/sT" +
            "iXePaAZ/Gh1GKXTq7k6ab/67MMeVFp/EdySumqdWLtvceFKs/gcDAo11YYCae/K2" +
            "1uKGJ/uU4b4QHYnPIsAdYpuo5HIdoAOL/WwduRa8C6vSFrtMJLDqPK3BUpMz3CXN" +
            "GyMhjuaHKP5MPbBZkIfgUGZO5qvU9+i0UFRlc3QgRUNEU0EtRUNESCAoS2V5IGFu" +
            "ZCBzdWJrZXkgYXJlIDI1NiBiaXRzIGxvbmcpIDx0ZXN0LmVjZHNhLmVjZGhAZXhh" +
            "bXBsZS5jb20+iHoEExMIACIFAlG+BsACGwMGCwkIBwMCBhUIAgkKCwQWAgMBAh4B" +
            "AheAAAoJEPfAOVaMWj1Tk7YBAOLz007oltog5sLreMwxayIBpAesEI+QNkuJuA2/" +
            "vcMuAP99qi4fhhkmz5w41AE9Wc/VtobU2lZHmmk3clQsVyuFyg==");

    byte[] testMessage =
        Base64.decode(
            "hH4Dp5+FdoujIBwSAgMErx4BSvgXY3irwthgxU8zPoAoR+8rhmxdpwbw6ZJAO2GX" +
            "azWJ85JNcobHKDeGeUq6wkTFu+g6yG99gIX8J5xJAjBRhyCRcaFgwbdDV4orWTe3" +
            "iewiT8qs4BQ23e0c8t+thdKoK4thMsCJy7wSKqY0sJTSVAELroNbCOi2lcO15YmW" +
            "6HiuFH7VKWcxPUBjXwf5+Z3uOKEp28tBgNyDrdbr1BbqlgYzIKq/pe9zUbUXfitn" +
            "vFc6HcGhvmRQreQ+Yw1x3x0HJeoPwg==");

    private void generate()
        throws Exception
    {
        //
        // Generate a master key
        //
        KeyPairGenerator        keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");

        keyGen.initialize(new ECGenParameterSpec("P-256"));

        KeyPair kpSign = keyGen.generateKeyPair();

        PGPKeyPair ecdsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.ECDSA, kpSign, new Date());

        //
        // Generate an encryption key
        //
        keyGen = KeyPairGenerator.getInstance("ECDH", "BC");

        keyGen.initialize(new ECGenParameterSpec("P-256"));

        KeyPair kpEnc = keyGen.generateKeyPair();

        PGPKeyPair ecdhKeyPair = new JcaPGPKeyPair(PGPPublicKey.ECDH, kpEnc, new Date());

        //
        // generate a key ring
        //
        char[] passPhrase = "test".toCharArray();
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, ecdsaKeyPair,
                 "test@bouncycastle.org", sha1Calc, null, null,
                 new JcaPGPContentSignerBuilder(ecdsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                 new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("BC").build(passPhrase));

        keyRingGen.addSubKey(ecdhKeyPair);

        PGPPublicKeyRing pubRing = keyRingGen.generatePublicKeyRing();

        // TODO: add check of KdfParameters
        doBasicKeyRingCheck(pubRing);

        PGPSecretKeyRing secRing = keyRingGen.generateSecretKeyRing();

        KeyFingerPrintCalculator fingerCalc = new JcaKeyFingerprintCalculator();

        PGPPublicKeyRing pubRingEnc = new PGPPublicKeyRing(pubRing.getEncoded(), fingerCalc);

        if (!Arrays.areEqual(pubRing.getEncoded(), pubRingEnc.getEncoded()))
        {
            fail("public key ring encoding failed");
        }

        PGPSecretKeyRing secRingEnc = new PGPSecretKeyRing(secRing.getEncoded(), fingerCalc);

        if (!Arrays.areEqual(secRing.getEncoded(), secRingEnc.getEncoded()))
        {
            fail("secret key ring encoding failed");
        }

        PGPPrivateKey pgpPrivKey = secRing.getSecretKey().extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passPhrase));
    }

    private void testDecrypt(PGPSecretKeyRing secretKeyRing)
        throws Exception
    {
        JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(testMessage);

        PGPEncryptedDataList encList = (PGPEncryptedDataList)pgpF.nextObject();

        PGPPublicKeyEncryptedData encP = (PGPPublicKeyEncryptedData)encList.get(0);

        PGPSecretKey secretKey = secretKeyRing.getSecretKey(); // secretKeyRing.getSecretKey(encP.getKeyID());

//        PGPPrivateKey pgpPrivKey = secretKey.extractPrivateKey(new JcePBESecretKeyEncryptorBuilder());

//        clear = encP.getDataStream(pgpPrivKey, "BC");
//
//        bOut.reset();
//
//        while ((ch = clear.read()) >= 0)
//        {
//            bOut.write(ch);
//        }
//
//        out = bOut.toByteArray();
//
//        if (!areEqual(out, text))
//        {
//            fail("wrong plain text in generated packet");
//        }
    }

    private void encryptDecryptTest()
        throws Exception
    {
        byte[]    text = { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o', (byte)'r', (byte)'l', (byte)'d', (byte)'!', (byte)'\n' };


        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");

        keyGen.initialize(new ECGenParameterSpec("P-256"));

        KeyPair kpEnc = keyGen.generateKeyPair();

        PGPKeyPair ecdhKeyPair = new JcaPGPKeyPair(PGPPublicKey.ECDH, kpEnc, new Date());

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        ByteArrayOutputStream   ldOut = new ByteArrayOutputStream();
        OutputStream pOut = lData.open(ldOut, PGPLiteralDataGenerator.UTF8, PGPLiteralData.CONSOLE, text.length, new Date());

        pOut.write(text);

        pOut.close();

        byte[] data = ldOut.toByteArray();

        ByteArrayOutputStream cbOut = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.CAST5).setProvider("BC").setSecureRandom(new SecureRandom()));

        cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(ecdhKeyPair.getPublicKey()).setProvider("BC"));

        OutputStream cOut = cPk.open(new UncloseableOutputStream(cbOut), data.length);

        cOut.write(data);

        cOut.close();

        JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(cbOut.toByteArray());

        PGPEncryptedDataList encList = (PGPEncryptedDataList)pgpF.nextObject();

        PGPPublicKeyEncryptedData encP = (PGPPublicKeyEncryptedData)encList.get(0);

        InputStream clear = encP.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(ecdhKeyPair.getPrivateKey()));

        pgpF = new JcaPGPObjectFactory(clear);

        PGPLiteralData ld = (PGPLiteralData)pgpF.nextObject();

        clear = ld.getInputStream();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        int ch;
        while ((ch = clear.read()) >= 0)
        {
            bOut.write(ch);
        }

        byte[] out = bOut.toByteArray();

        if (!areEqual(out, text))
        {
            fail("wrong plain text in generated packet");
        }
    }

    private void encryptDecryptBCTest()
        throws Exception
    {
        byte[]    text = { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o', (byte)'r', (byte)'l', (byte)'d', (byte)'!', (byte)'\n' };


        ECKeyPairGenerator keyGen = new ECKeyPairGenerator();

        X9ECParameters x9ECParameters = NISTNamedCurves.getByName("P-256");
        keyGen.init(new ECKeyGenerationParameters(new ECNamedDomainParameters(NISTNamedCurves.getOID("P-256"), x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN()), new SecureRandom()));

        AsymmetricCipherKeyPair kpEnc = keyGen.generateKeyPair();

        PGPKeyPair ecdhKeyPair = new BcPGPKeyPair(PGPPublicKey.ECDH, kpEnc, new Date());

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        ByteArrayOutputStream   ldOut = new ByteArrayOutputStream();
        OutputStream pOut = lData.open(ldOut, PGPLiteralDataGenerator.UTF8, PGPLiteralData.CONSOLE, text.length, new Date());

        pOut.write(text);

        pOut.close();

        byte[] data = ldOut.toByteArray();

        ByteArrayOutputStream cbOut = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.CAST5).setSecureRandom(new SecureRandom()));

        cPk.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(ecdhKeyPair.getPublicKey()));

        OutputStream cOut = cPk.open(new UncloseableOutputStream(cbOut), data.length);

        cOut.write(data);

        cOut.close();

        JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(cbOut.toByteArray());

        PGPEncryptedDataList encList = (PGPEncryptedDataList)pgpF.nextObject();

        PGPPublicKeyEncryptedData encP = (PGPPublicKeyEncryptedData)encList.get(0);

        InputStream clear = encP.getDataStream(new BcPublicKeyDataDecryptorFactory(ecdhKeyPair.getPrivateKey()));

        pgpF = new JcaPGPObjectFactory(clear);

        PGPLiteralData ld = (PGPLiteralData)pgpF.nextObject();

        clear = ld.getInputStream();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        int ch;
        while ((ch = clear.read()) >= 0)
        {
            bOut.write(ch);
        }

        byte[] out = bOut.toByteArray();

        if (!areEqual(out, text))
        {
            fail("wrong plain text in generated packet");
        }
    }

    public void performTest()
        throws Exception
    {
        //
        // Read the public key
        //
        PGPPublicKeyRing        pubKeyRing = new PGPPublicKeyRing(testPubKey, new JcaKeyFingerprintCalculator());

        doBasicKeyRingCheck(pubKeyRing);

        //
        // Read the private key
        //
        PGPSecretKeyRing        secretKeyRing = new PGPSecretKeyRing(testPrivKey, new JcaKeyFingerprintCalculator());

        testDecrypt(secretKeyRing);

        encryptDecryptTest();
        encryptDecryptBCTest();

        generate();
    }

    private void doBasicKeyRingCheck(PGPPublicKeyRing pubKeyRing)
        throws PGPException, SignatureException
    {
        for (Iterator it = pubKeyRing.getPublicKeys(); it.hasNext();)
        {
            PGPPublicKey pubKey = (PGPPublicKey)it.next();

            if (pubKey.isMasterKey())
            {
                if (pubKey.isEncryptionKey())
                {
                    fail("master key showed as encryption key!");
                }
            }
            else
            {
                if (!pubKey.isEncryptionKey())
                {
                    fail("sub key not encryption key!");
                }

                for (Iterator sigIt = pubKeyRing.getPublicKey().getSignatures(); sigIt.hasNext();)
                {
                    PGPSignature certification = (PGPSignature)sigIt.next();

                    certification.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), pubKeyRing.getPublicKey());

                    if (!certification.verifyCertification((String)pubKeyRing.getPublicKey().getUserIDs().next(), pubKeyRing.getPublicKey()))
                    {
                        fail("subkey certification does not verify");
                    }
                }
            }
        }
    }

    public String getName()
    {
        return "PGPECDHTest";
    }

    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        runTest(new PGPECDHTest());
    }
}
