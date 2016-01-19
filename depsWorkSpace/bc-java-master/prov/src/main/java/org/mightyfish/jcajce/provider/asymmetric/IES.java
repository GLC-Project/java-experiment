package org.mightyfish.jcajce.provider.asymmetric;

import org.mightyfish.jcajce.provider.config.ConfigurableProvider;
import org.mightyfish.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class IES
{
    private static final String PREFIX = "org.mightyfish.jcajce.provider.asymmetric" + ".ies.";

    public static class Mappings
        extends AsymmetricAlgorithmProvider
    {
        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {
            provider.addAlgorithm("AlgorithmParameters.IES", PREFIX + "AlgorithmParametersSpi");
            provider.addAlgorithm("Cipher.IES", PREFIX + "CipherSpi$IES");
        }
    }
}
