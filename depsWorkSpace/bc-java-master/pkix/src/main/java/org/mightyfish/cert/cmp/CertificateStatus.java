package org.mightyfish.cert.cmp;

import java.math.BigInteger;

import org.mightyfish.asn1.cmp.CertStatus;
import org.mightyfish.asn1.cmp.PKIStatusInfo;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.cert.X509CertificateHolder;
import org.mightyfish.operator.DigestAlgorithmIdentifierFinder;
import org.mightyfish.operator.DigestCalculator;
import org.mightyfish.operator.DigestCalculatorProvider;
import org.mightyfish.operator.OperatorCreationException;
import org.mightyfish.util.Arrays;

public class CertificateStatus
{
    private DigestAlgorithmIdentifierFinder digestAlgFinder;    
    private CertStatus certStatus;

    CertificateStatus(DigestAlgorithmIdentifierFinder digestAlgFinder, CertStatus certStatus)
    {
        this.digestAlgFinder = digestAlgFinder;
        this.certStatus = certStatus;
    }

    public PKIStatusInfo getStatusInfo()
    {
        return certStatus.getStatusInfo();
    }

    public BigInteger getCertRequestID()
    {
        return certStatus.getCertReqId().getValue();
    }

    public boolean isVerified(X509CertificateHolder certHolder, DigestCalculatorProvider digesterProvider)
        throws CMPException
    {
        AlgorithmIdentifier digAlg = digestAlgFinder.find(certHolder.toASN1Structure().getSignatureAlgorithm());
        if (digAlg == null)
        {
            throw new CMPException("cannot find algorithm for digest from signature");
        }

        DigestCalculator digester;

        try
        {
            digester = digesterProvider.get(digAlg);
        }
        catch (OperatorCreationException e)
        {
            throw new CMPException("unable to create digester: " + e.getMessage(), e);
        }

        CMPUtil.derEncodeToStream(certHolder.toASN1Structure(), digester.getOutputStream());

        return Arrays.areEqual(certStatus.getCertHash().getOctets(), digester.getDigest());
    }
}
