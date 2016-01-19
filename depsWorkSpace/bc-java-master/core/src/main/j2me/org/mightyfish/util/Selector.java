package org.mightyfish.util;

public interface Selector
{
    boolean match(Object obj);

    Object clone();
}
