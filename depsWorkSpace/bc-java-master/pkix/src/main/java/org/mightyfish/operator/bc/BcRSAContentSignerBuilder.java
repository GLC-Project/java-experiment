package org.mightyfish.operator.bc;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.Signer;
import org.mightyfish.crypto.signers.RSADigestSigner;
import org.mightyfish.operator.OperatorCreationException;

public class BcRSAContentSignerBuilder
    extends BcContentSignerBuilder
{
    public BcRSAContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId)
    {
        super(sigAlgId, digAlgId);
    }

    protected Signer createSigner(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId)
        throws OperatorCreationException
    {
        Digest dig = digestProvider.get(digAlgId);

        return new RSADigestSigner(dig);
    }
}
