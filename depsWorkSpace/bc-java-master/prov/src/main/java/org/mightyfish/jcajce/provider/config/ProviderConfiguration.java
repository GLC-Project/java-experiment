package org.mightyfish.jcajce.provider.config;

import javax.crypto.spec.DHParameterSpec;

import org.mightyfish.jce.spec.ECParameterSpec;

public interface ProviderConfiguration
{
    ECParameterSpec getEcImplicitlyCa();

    DHParameterSpec getDHDefaultParameters(int keySize);
}
