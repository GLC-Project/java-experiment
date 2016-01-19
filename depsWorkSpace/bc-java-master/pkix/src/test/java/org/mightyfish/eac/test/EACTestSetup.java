
package org.mightyfish.eac.test;

import java.security.Security;

import junit.extensions.TestSetup;
import junit.framework.Test;
import org.mightyfish.jce.provider.BouncyCastleProvider;

class EACTestSetup
    extends TestSetup
{
    public EACTestSetup(Test test)
    {
        super(test);
    }

    protected void setUp()
    {
        Security.addProvider(new org.mightyfish.jce.provider.BouncyCastleProvider());
    }

    protected void tearDown()
    {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

}
