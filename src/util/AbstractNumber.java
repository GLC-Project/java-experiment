package util;

/**
 *
 * @author bogachenko - OpenAHP - Omsk Java User Group - GPL 3.0
 */

public interface AbstractNumber {
    /**
     * @param b
     * @return
     */
    AbstractNumber add(AbstractNumber b);

    /**
     * @param b
     * @return
     */
    AbstractNumber sub(AbstractNumber b);

    /**
     * @param b
     * @return
     */
    AbstractNumber mult(AbstractNumber b);

    /**
     * @param b
     * @return
     */
    AbstractNumber div(AbstractNumber b);
}
