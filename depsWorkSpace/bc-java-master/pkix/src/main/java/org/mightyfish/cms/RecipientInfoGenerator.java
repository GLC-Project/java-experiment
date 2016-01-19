package org.mightyfish.cms;

import org.mightyfish.asn1.cms.RecipientInfo;
import org.mightyfish.operator.GenericKey;

public interface RecipientInfoGenerator
{
    RecipientInfo generate(GenericKey contentEncryptionKey)
        throws CMSException;
}
