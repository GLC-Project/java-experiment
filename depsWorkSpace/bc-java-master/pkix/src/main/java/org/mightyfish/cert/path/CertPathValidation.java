package org.mightyfish.cert.path;

import org.mightyfish.cert.X509CertificateHolder;
import org.mightyfish.util.Memoable;

public interface CertPathValidation
    extends Memoable
{
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate)
        throws CertPathValidationException;
}
