package org.mightyfish.asn1.smime;

import org.mightyfish.asn1.DERSequence;
import org.mightyfish.asn1.DERSet;
import org.mightyfish.asn1.cms.Attribute;

public class SMIMECapabilitiesAttribute
    extends Attribute
{
    public SMIMECapabilitiesAttribute(
        SMIMECapabilityVector capabilities)
    {
        super(SMIMEAttributes.smimeCapabilities,
                new DERSet(new DERSequence(capabilities.toASN1EncodableVector())));
    }
}
