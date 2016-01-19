package org.mightyfish.mail.smime.examples;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.mightyfish.cms.RecipientId;
import org.mightyfish.cms.RecipientInformation;
import org.mightyfish.cms.RecipientInformationStore;
import org.mightyfish.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.mightyfish.cms.jcajce.JceKeyTransRecipientId;
import org.mightyfish.mail.smime.SMIMEEnvelopedParser;
import org.mightyfish.mail.smime.SMIMEUtil;
import org.mightyfish.mail.smime.util.SharedFileInputStream;

/**
 * a simple example that reads an encrypted email using the large file model.
 * <p>
 * The key store can be created using the class in
 * org.mightyfish.jce.examples.PKCS12Example - the program expects only one
 * key to be present.
 */
public class ReadLargeEncryptedMail
{
    public static void main(
        String args[])
        throws Exception
    {
        if (args.length != 3)
        {
            System.err.println("usage: ReadLargeEncryptedMail pkcs12Keystore password outputFile");
            System.exit(0);
        }

        //
        // Open the key store
        //
        KeyStore    ks = KeyStore.getInstance("PKCS12", "BC");
        String      keyAlias = ExampleUtils.findKeyAlias(ks, args[0], args[1].toCharArray());

        //
        // find the certificate for the private key and generate a 
        // suitable recipient identifier.
        //
        X509Certificate cert = (X509Certificate)ks.getCertificate(keyAlias);
        RecipientId     recId = new JceKeyTransRecipientId(cert);

        //
        // Get a Session object with the default properties.
        //         
        Properties props = System.getProperties();

        Session session = Session.getDefaultInstance(props, null);

        MimeMessage msg = new MimeMessage(session, new SharedFileInputStream("encrypted.message"));

        SMIMEEnvelopedParser       m = new SMIMEEnvelopedParser(msg);

        RecipientInformationStore   recipients = m.getRecipientInfos();
        RecipientInformation        recipient = recipients.get(recId);

        MimeBodyPart        res = SMIMEUtil.toMimeBodyPart(recipient.getContentStream(new JceKeyTransEnvelopedRecipient((PrivateKey)ks.getKey(keyAlias, null)).setProvider("BC")));

        ExampleUtils.dumpContent(res, args[2]);
    }
}
