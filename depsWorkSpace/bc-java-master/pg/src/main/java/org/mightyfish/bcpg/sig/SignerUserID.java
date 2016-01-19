package org.mightyfish.bcpg.sig;

import org.mightyfish.bcpg.SignatureSubpacket;
import org.mightyfish.bcpg.SignatureSubpacketTags;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.Strings;

/**
 * packet giving the User ID of the signer.
 */
public class SignerUserID 
    extends SignatureSubpacket
{
    public SignerUserID(
        boolean    critical,
        byte[]     data)
    {
        super(SignatureSubpacketTags.SIGNER_USER_ID, critical, data);
    }
    
    public SignerUserID(
        boolean    critical,
        String     userID)
    {
        super(SignatureSubpacketTags.SIGNER_USER_ID, critical, Strings.toUTF8ByteArray(userID));
    }
    
    public String getID()
    {
        return Strings.fromUTF8ByteArray(data);
    }

    public byte[] getRawID()
    {
        return Arrays.clone(data);
    }
}
