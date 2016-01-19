package org.mightyfish.openpgp.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import org.mightyfish.jce.spec.ECNamedCurveGenParameterSpec;
import java.util.Date;
import java.util.Iterator;

import org.mightyfish.bcpg.HashAlgorithmTags;
import org.mightyfish.jce.provider.BouncyCastleProvider;
import org.mightyfish.openpgp.PGPEncryptedData;
import org.mightyfish.openpgp.PGPKeyPair;
import org.mightyfish.openpgp.PGPKeyRingGenerator;
import org.mightyfish.openpgp.PGPPublicKey;
import org.mightyfish.openpgp.PGPPublicKeyRing;
import org.mightyfish.openpgp.PGPSecretKeyRing;
import org.mightyfish.openpgp.PGPSignature;
import org.mightyfish.openpgp.PGPSignatureGenerator;
import org.mightyfish.openpgp.PGPUtil;
import org.mightyfish.openpgp.operator.KeyFingerPrintCalculator;
import org.mightyfish.openpgp.operator.PGPDigestCalculator;
import org.mightyfish.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.mightyfish.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.mightyfish.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Base64;
import org.mightyfish.util.test.SimpleTest;

public class PGPECDSATest
    extends SimpleTest
{
    byte[] testPubKey =
        Base64.decode(
            "mFIEUb4HqBMIKoZIzj0DAQcCAwSQynmjwsGJHYJakAEVYxrm3tt/1h8g9Uksx32J" +
            "zG/ZH4RwaD0PbjzEe5EVBmCwSErRZxt/5AxXa0TEHWjya8FetDVFQ0RTQSAoS2V5" +
            "IGlzIDI1NiBiaXRzIGxvbmcpIDx0ZXN0LmVjZHNhQGV4YW1wbGUuY29tPoh6BBMT" +
            "CAAiBQJRvgeoAhsDBgsJCAcDAgYVCAIJCgsEFgIDAQIeAQIXgAAKCRDqO46kgPLi" +
            "vN1hAP4n0UApR36ziS5D8KUt7wEpBujQE4G3+efATJ+DMmY/SgEA+wbdDynFf/V8" +
            "pQs0+FtCYQ9schzIur+peRvol7OrNnc=");

    byte[] testPrivKey =
        Base64.decode(
            "lKUEUb4HqBMIKoZIzj0DAQcCAwSQynmjwsGJHYJakAEVYxrm3tt/1h8g9Uksx32J" +
            "zG/ZH4RwaD0PbjzEe5EVBmCwSErRZxt/5AxXa0TEHWjya8Fe/gcDAqTWSUiFpEno" +
            "1n8izmLaWTy8GYw5/lK4R2t6D347YGgTtIiXfoNPOcosmU+3OibyTm2hc/WyG4fL" +
            "a0nxFtj02j0Bt/Fw0N4VCKJwKL/QJT+0NUVDRFNBIChLZXkgaXMgMjU2IGJpdHMg" +
            "bG9uZykgPHRlc3QuZWNkc2FAZXhhbXBsZS5jb20+iHoEExMIACIFAlG+B6gCGwMG" +
            "CwkIBwMCBhUIAgkKCwQWAgMBAh4BAheAAAoJEOo7jqSA8uK83WEA/ifRQClHfrOJ" +
            "LkPwpS3vASkG6NATgbf558BMn4MyZj9KAQD7Bt0PKcV/9XylCzT4W0JhD2xyHMi6" +
            "v6l5G+iXs6s2dw==");

    private void generateAndSign()
        throws Exception
    {
        KeyPairGenerator        keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");

        keyGen.initialize(new ECNamedCurveGenParameterSpec("P-256"));

        KeyPair kpSign = keyGen.generateKeyPair();

        PGPKeyPair ecdsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.ECDSA, kpSign, new Date());

        //
        // try a signature
        //
        PGPSignatureGenerator signGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(PGPPublicKey.ECDSA, HashAlgorithmTags.SHA256).setProvider("BC"));

        signGen.init(PGPSignature.BINARY_DOCUMENT, ecdsaKeyPair.getPrivateKey());

        signGen.update("hello world!".getBytes());

        PGPSignature sig = signGen.generate();

        sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), ecdsaKeyPair.getPublicKey());

        sig.update("hello world!".getBytes());

        if (!sig.verify())
        {
            fail("signature failed to verify!");
        }

        //
        // generate a key ring
        //
        char[] passPhrase = "test".toCharArray();
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, ecdsaKeyPair,
                 "test@bouncycastle.org", sha1Calc, null, null, new JcaPGPContentSignerBuilder(ecdsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("BC").build(passPhrase));

        PGPPublicKeyRing pubRing = keyRingGen.generatePublicKeyRing();

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
    }

    public void performTest()
        throws Exception
    {
        //
        // Read the public key
        //
        PGPPublicKeyRing        pubKeyRing = new PGPPublicKeyRing(testPubKey, new JcaKeyFingerprintCalculator());

        for (Iterator it = pubKeyRing.getPublicKey().getSignatures(); it.hasNext();)
        {
            PGPSignature certification = (PGPSignature)it.next();

            certification.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), pubKeyRing.getPublicKey());

            if (!certification.verifyCertification((String)pubKeyRing.getPublicKey().getUserIDs().next(), pubKeyRing.getPublicKey()))
            {
                fail("self certification does not verify");
            }
        }

        //
        // Read the private key
        //
        PGPSecretKeyRing        secretKeyRing = new PGPSecretKeyRing(testPrivKey, new JcaKeyFingerprintCalculator());


        generateAndSign();
    }

    public String getName()
    {
        return "PGPECDSATest";
    }

    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        runTest(new PGPECDSATest());
    }
}
