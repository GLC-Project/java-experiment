package org.mightyfish.operator.bc;

import java.io.IOException;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.asn1.x509.SubjectPublicKeyInfo;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.Signer;
import org.mightyfish.crypto.params.AsymmetricKeyParameter;
import org.mightyfish.crypto.signers.RSADigestSigner;
import org.mightyfish.crypto.util.PublicKeyFactory;
import org.mightyfish.operator.DigestAlgorithmIdentifierFinder;
import org.mightyfish.operator.OperatorCreationException;

public class BcRSAContentVerifierProviderBuilder
    extends BcContentVerifierProviderBuilder
{
    private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;

    public BcRSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder digestAlgorithmFinder)
    {
        this.digestAlgorithmFinder = digestAlgorithmFinder;
    }

    protected Signer createSigner(AlgorithmIdentifier sigAlgId)
        throws OperatorCreationException
    {
        AlgorithmIdentifier digAlg = digestAlgorithmFinder.find(sigAlgId);
        Digest dig = digestProvider.get(digAlg);

        return new RSADigestSigner(dig);
    }

    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo publicKeyInfo)
        throws IOException
    {
        return PublicKeyFactory.createKey(publicKeyInfo);
    }
}