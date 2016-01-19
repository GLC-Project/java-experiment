package org.mightyfish.operator.bc;

import org.mightyfish.asn1.x509.AlgorithmIdentifier;
import org.mightyfish.crypto.Digest;
import org.mightyfish.crypto.Signer;
import org.mightyfish.crypto.signers.DSADigestSigner;
import org.mightyfish.crypto.signers.DSASigner;
import org.mightyfish.operator.OperatorCreationException;

public class BcDSAContentSignerBuilder
    extends BcContentSignerBuilder
{
    public BcDSAContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId)
    {
        super(sigAlgId, digAlgId);
    }

    protected Signer createSigner(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId)
        throws OperatorCreationException
    {
        Digest dig = digestProvider.get(digAlgId);

        return new DSADigestSigner(new DSASigner(), dig);
    }
}
