package testing;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 *
 * @author Zacheusz
 */
@SuppressWarnings("restriction")
public class ReplaceCertFactorySpi extends sun.security.provider.X509Factory {

    private static final Certificate MATCH;
    private static final Certificate REPLACEMENT;

    static {
	try {
	    CertificateFactory factory = CertificateFactory.getInstance(
		    "X.509", "SUN"); //$NON-NLS-1$ //$NON-NLS-2$
	    InputStream matchIs = ReplaceCertFactorySpi.class
		    .getResourceAsStream("match.crt"); //$NON-NLS-1$
	    assert matchIs != null;
	    MATCH = factory.generateCertificate(matchIs);
	    assert MATCH != null;
	    InputStream replacementIs = ReplaceCertFactorySpi.class
		    .getResourceAsStream("replacement.crt"); //$NON-NLS-1$
	    assert replacementIs != null;
	    REPLACEMENT = factory.generateCertificate(replacementIs);
	    assert REPLACEMENT != null;
	} catch (Exception ex) {
	    throw new RuntimeException(ex);
	}
    }

    @Override
    public Certificate engineGenerateCertificate(InputStream in)
	    throws CertificateException {
	Certificate cert = super.engineGenerateCertificate(in);
	if (MATCH.equals(cert)) {
	    System.out.println("Replacing jce code signing cert!"); //$NON-NLS-1$
	    cert = REPLACEMENT;
	}
	return cert;
    }
}
