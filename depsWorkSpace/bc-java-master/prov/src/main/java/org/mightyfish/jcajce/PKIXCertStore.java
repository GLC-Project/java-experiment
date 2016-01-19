package org.mightyfish.jcajce;

import java.util.Collection;

import org.mightyfish.util.Selector;
import org.mightyfish.util.Store;
import org.mightyfish.util.StoreException;

public interface PKIXCertStore
    extends Store
{
    Collection getMatches(Selector selector)
        throws StoreException;
}
