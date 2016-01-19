package org.mightyfish.asn1.smime;

import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.pkcs.PKCSObjectIdentifiers;

public interface SMIMEAttributes
{
    public static final ASN1ObjectIdentifier  smimeCapabilities = PKCSObjectIdentifiers.pkcs_9_at_smimeCapabilities;
    public static final ASN1ObjectIdentifier  encrypKeyPref = PKCSObjectIdentifiers.id_aa_encrypKeyPref;
}
