package org.mightyfish.openpgp.operator;

import org.mightyfish.bcpg.HashAlgorithmTags;
import org.mightyfish.openpgp.PGPException;

/**
 * A factory for digest algorithms.
 */
public interface PGPDigestCalculatorProvider
{
    /**
     * Construct a new instance of a cryptographic digest.
     * 
     * @param algorithm the identifier of the {@link HashAlgorithmTags digest algorithm} to
     *            instantiate.
     * @return a digest calculator for the specified algorithm.
     * @throws PGPException if an error occurs constructing the specified digest.
     */
    PGPDigestCalculator get(int algorithm)
        throws PGPException;
}
