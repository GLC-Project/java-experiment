package org.mightyfish.operator.bc;

import org.mightyfish.asn1.kisa.KISAObjectIdentifiers;
import org.mightyfish.asn1.x509.AlgorithmIdentifier;

class SEEDUtil
{
    static AlgorithmIdentifier determineKeyEncAlg()
    {
        // parameters absent
        return new AlgorithmIdentifier(
            KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap);
    }
}
