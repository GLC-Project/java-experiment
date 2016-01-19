package org.mightyfish.crypto.test;

import org.mightyfish.crypto.Mac;
import org.mightyfish.crypto.engines.GOST28147Engine;
import org.mightyfish.crypto.macs.GOST28147Mac;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.crypto.params.ParametersWithSBox;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Hex;
import org.mightyfish.util.test.SimpleTestResult;
import org.mightyfish.util.test.Test;
import org.mightyfish.util.test.TestResult;

/**
 * GOST 28147 MAC tester 
 */
public class GOST28147MacTest
    implements Test
{
    //
    // these GOSTMac for testing.
    //
    static byte[]   gkeyBytes1 = Hex.decode("6d145dc993f4019e104280df6fcd8cd8e01e101e4c113d7ec4f469ce6dcd9e49");
    static byte[]   gkeyBytes2 = Hex.decode("6d145dc993f4019e104280df6fcd8cd8e01e101e4c113d7ec4f469ce6dcd9e49");

    static byte[]   input3 = Hex.decode("7768617420646f2079612077616e7420666f72206e6f7468696e673f");
    static byte[]   input4 = Hex.decode("7768617420646f2079612077616e7420666f72206e6f7468696e673f");

    static byte[]   output7 = Hex.decode("93468a46");
    static byte[]   output8 = Hex.decode("93468a46");

    public GOST28147MacTest()
    {
    }

    public TestResult perform()
    {
        // test1
        Mac          mac = new GOST28147Mac();
        KeyParameter key = new KeyParameter(gkeyBytes1);

        mac.init(key);

        mac.update(input3, 0, input3.length);

        byte[] out = new byte[4];

        mac.doFinal(out, 0);

        if (!Arrays.areEqual(out, output7))
        {
            return new SimpleTestResult(false, getName() + ": Failed test 1 - expected " + new String(Hex.encode(output7)) + " got " + new String(Hex.encode(out)));
        }

        // test2
        key = new KeyParameter(gkeyBytes2);

        ParametersWithSBox gparam = new ParametersWithSBox(key, GOST28147Engine.getSBox("E-A"));

        mac.init(gparam);

        mac.update(input4, 0, input4.length);

        out = new byte[4];

        mac.doFinal(out, 0);

        if (!Arrays.areEqual(out, output8))
        {
            return new SimpleTestResult(false, getName() + ": Failed test 2 - expected " + new String(Hex.encode(output8)) + " got " + new String(Hex.encode(out)));
        }

        return new SimpleTestResult(true, getName() + ": Okay");
    }

    public String getName()
    {
        return "GOST28147Mac";
    }

    public static void main(
        String[]    args)
    {
        GOST28147MacTest    test = new GOST28147MacTest();
        TestResult result = test.perform();

        System.out.println(result);
    }
}
