package org.mightyfish.crypto.tls.test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import org.mightyfish.asn1.ASN1EncodableVector;
import org.mightyfish.asn1.DERBitString;
import org.mightyfish.asn1.DERSequence;
import org.mightyfish.asn1.x509.Certificate;
import org.mightyfish.crypto.tls.AlertDescription;
import org.mightyfish.crypto.tls.AlertLevel;
import org.mightyfish.crypto.tls.CertificateRequest;
import org.mightyfish.crypto.tls.ClientCertificateType;
import org.mightyfish.crypto.tls.ConnectionEnd;
import org.mightyfish.crypto.tls.DefaultTlsClient;
import org.mightyfish.crypto.tls.ProtocolVersion;
import org.mightyfish.crypto.tls.SignatureAlgorithm;
import org.mightyfish.crypto.tls.SignatureAndHashAlgorithm;
import org.mightyfish.crypto.tls.TlsAuthentication;
import org.mightyfish.crypto.tls.TlsCredentials;
import org.mightyfish.crypto.tls.TlsFatalAlert;
import org.mightyfish.crypto.tls.TlsSignerCredentials;
import org.mightyfish.util.Arrays;

class TlsTestClientImpl
    extends DefaultTlsClient
{
    protected final TlsTestConfig config;

    protected int firstFatalAlertConnectionEnd = -1;
    protected short firstFatalAlertDescription = -1;

    TlsTestClientImpl(TlsTestConfig config)
    {
        this.config = config;
    }

    int getFirstFatalAlertConnectionEnd()
    {
        return firstFatalAlertConnectionEnd;
    }

    short getFirstFatalAlertDescription()
    {
        return firstFatalAlertDescription;
    }

    public ProtocolVersion getClientVersion()
    {
        if (config.clientOfferVersion != null)
        {
            return config.clientOfferVersion;
        }

        return super.getClientVersion();
    }

    public ProtocolVersion getMinimumVersion()
    {
        if (config.clientMinimumVersion != null)
        {
            return config.clientMinimumVersion;
        }

        return super.getMinimumVersion();
    }

    public boolean isFallback()
    {
        return config.clientFallback;
    }

    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
    
    {
        if (alertLevel == AlertLevel.fatal && firstFatalAlertConnectionEnd == -1)
        {
            firstFatalAlertConnectionEnd = ConnectionEnd.client;
            firstFatalAlertDescription = alertDescription;
        }

        if (TlsTestConfig.DEBUG)
        {
            PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
            out.println("TLS client raised alert (AlertLevel." + alertLevel + ", AlertDescription." + alertDescription
                + ")");
            if (message != null)
            {
                out.println("> " + message);
            }
            if (cause != null)
            {
                cause.printStackTrace(out);
            }
        }
    }

    public void notifyAlertReceived(short alertLevel, short alertDescription)
    {
        if (alertLevel == AlertLevel.fatal && firstFatalAlertConnectionEnd == -1)
        {
            firstFatalAlertConnectionEnd = ConnectionEnd.server;
            firstFatalAlertDescription = alertDescription;
        }

        if (TlsTestConfig.DEBUG)
        {
            PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
            out.println("TLS client received alert (AlertLevel." + alertLevel + ", AlertDescription."
                + alertDescription + ")");
        }
    }

    public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException
    {
        super.notifyServerVersion(serverVersion);

        if (TlsTestConfig.DEBUG)
        {
            System.out.println("TLS client negotiated " + serverVersion);
        }
    }

    public TlsAuthentication getAuthentication()
        throws IOException
    {
        return new TlsAuthentication()
        {
            public void notifyServerCertificate(org.mightyfish.crypto.tls.Certificate serverCertificate)
                throws IOException
            {
                boolean isEmpty = serverCertificate == null || serverCertificate.isEmpty();

                Certificate[] chain = serverCertificate.getCertificateList();

                if (isEmpty || !chain[0].equals(TlsTestUtils.loadCertificateResource("x509-server.pem")))
                {
                    throw new TlsFatalAlert(AlertDescription.bad_certificate);
                }

                if (TlsTestConfig.DEBUG)
                {
                    System.out.println("TLS client received server certificate chain of length " + chain.length);
                    for (int i = 0; i != chain.length; i++)
                    {
                        Certificate entry = chain[i];
                        // TODO Create fingerprint based on certificate signature algorithm digest
                        System.out.println("    fingerprint:SHA-256 " + TlsTestUtils.fingerprint(entry) + " ("
                            + entry.getSubject() + ")");
                    }
                }
            }

            public TlsCredentials getClientCredentials(CertificateRequest certificateRequest)
                throws IOException
            {
                if (config.serverCertReq == TlsTestConfig.SERVER_CERT_REQ_NONE)
                {
                    throw new IllegalStateException();
                }
                if (config.clientAuth == TlsTestConfig.CLIENT_AUTH_NONE)
                {
                    return null;
                }

                short[] certificateTypes = certificateRequest.getCertificateTypes();
                if (certificateTypes == null || !Arrays.contains(certificateTypes, ClientCertificateType.rsa_sign))
                {
                    return null;
                }

                SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
                Vector sigAlgs = certificateRequest.getSupportedSignatureAlgorithms();
                if (sigAlgs != null)
                {
                    for (int i = 0; i < sigAlgs.size(); ++i)
                    {
                        SignatureAndHashAlgorithm sigAlg = (SignatureAndHashAlgorithm)
                            sigAlgs.elementAt(i);
                        if (sigAlg.getSignature() == SignatureAlgorithm.rsa)
                        {
                            signatureAndHashAlgorithm = sigAlg;
                            break;
                        }
                    }

                    if (signatureAndHashAlgorithm == null)
                    {
                        return null;
                    }
                }

                final TlsSignerCredentials signerCredentials = TlsTestUtils.loadSignerCredentials(context, new String[]{
                    "x509-client.pem", "x509-ca.pem" }, "x509-client-key.pem", signatureAndHashAlgorithm);

                if (config.clientAuth == TlsTestConfig.CLIENT_AUTH_VALID)
                {
                    return signerCredentials;
                }

                return new TlsSignerCredentials()
                {
                    public byte[] generateCertificateSignature(byte[] hash) throws IOException
                    {
                        byte[] sig = signerCredentials.generateCertificateSignature(hash);

                        if (config.clientAuth == TlsTestConfig.CLIENT_AUTH_INVALID_VERIFY)
                        {
                            sig = corruptBit(sig);
                        }

                        return sig;
                    }

                    public org.mightyfish.crypto.tls.Certificate getCertificate()
                    {
                        org.mightyfish.crypto.tls.Certificate cert = signerCredentials.getCertificate();

                        if (config.clientAuth == TlsTestConfig.CLIENT_AUTH_INVALID_CERT)
                        {
                            cert = corruptCertificate(cert);
                        }

                        return cert;
                    }

                    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm()
                    {
                        return signerCredentials.getSignatureAndHashAlgorithm();
                    }
                };
            }
        };
    }

    protected org.mightyfish.crypto.tls.Certificate corruptCertificate(org.mightyfish.crypto.tls.Certificate cert)
    {
        Certificate[] certList = cert.getCertificateList();
        certList[0] = corruptCertificateSignature(certList[0]);
        return new org.mightyfish.crypto.tls.Certificate(certList);
    }

    protected Certificate corruptCertificateSignature(Certificate cert)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(cert.getTBSCertificate());
        v.add(cert.getSignatureAlgorithm());
        v.add(corruptBitString(cert.getSignature()));

        return Certificate.getInstance(new DERSequence(v));
    }

    protected DERBitString corruptBitString(DERBitString bs)
    {
        return new DERBitString(corruptBit(bs.getBytes()));
    }

    protected byte[] corruptBit(byte[] bs)
    {
        bs = Arrays.clone(bs);

        // Flip a random bit
        int bit = context.getSecureRandom().nextInt(bs.length << 3); 
        bs[bit >>> 3] ^= (1 << (bit & 7));

        return bs;
    }
}
