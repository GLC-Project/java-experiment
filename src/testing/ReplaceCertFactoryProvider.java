package testing;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;

/**
 * @author Zacheusz
 */
@SuppressWarnings("serial")
public class ReplaceCertFactoryProvider extends Provider {
    /**
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ReplaceCertFactoryProvider() {
	super("ReplaceCertFactoryProvider", 1.0, //$NON-NLS-1$
		"Replace old jce code signing certificate."); //$NON-NLS-1$
	AccessController.doPrivileged(new PrivilegedAction() {
	    final ReplaceCertFactoryProvider p = ReplaceCertFactoryProvider.this;

	    @Override
	    public Object run() {
		System.out
			.println("Attempting to replace certificate factory provider.."); //$NON-NLS-1$
		p.put("CertificateFactory.X.509", "ReplaceCertFactorySpi"); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	    }
	});
    }

    public static void install() {
	Security.insertProviderAt(new ReplaceCertFactoryProvider(), 1);
    }
}
