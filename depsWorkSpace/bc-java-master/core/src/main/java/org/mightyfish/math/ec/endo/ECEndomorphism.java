package org.mightyfish.math.ec.endo;

import org.mightyfish.math.ec.ECPointMap;

public interface ECEndomorphism
{
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
