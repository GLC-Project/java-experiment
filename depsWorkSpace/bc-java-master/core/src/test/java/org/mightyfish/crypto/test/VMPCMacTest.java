package org.mightyfish.crypto.test;

import org.mightyfish.crypto.CipherParameters;
import org.mightyfish.crypto.macs.VMPCMac;
import org.mightyfish.crypto.params.KeyParameter;
import org.mightyfish.crypto.params.ParametersWithIV;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Hex;
import org.mightyfish.util.test.SimpleTest;

public class VMPCMacTest extends SimpleTest
{
    public String getName()
    {
        return "VMPC-MAC";
    }

    public static void main(String[] args)
    {
        runTest(new VMPCMacTest());
    }

    static byte[] output1 = Hex.decode("9BDA16E2AD0E284774A3ACBC8835A8326C11FAAD");

    public void performTest() throws Exception
    {
        CipherParameters kp = new KeyParameter(
            Hex.decode("9661410AB797D8A9EB767C21172DF6C7"));
        CipherParameters kpwiv = new ParametersWithIV(kp,
            Hex.decode("4B5C2F003E67F39557A8D26F3DA2B155"));

        byte[] m = new byte[256];
        for (int i = 0; i < 256; i++)
        {
            m[i] = (byte) i;
        }

        VMPCMac mac = new VMPCMac();
        mac.init(kpwiv);

        mac.update(m, 0, m.length);

        byte[] out = new byte[20];
        mac.doFinal(out, 0);

        if (!Arrays.areEqual(out, output1))
        {
            fail("Fail", new String(Hex.encode(output1)), new String(Hex.encode(out)));
        }
    }
}
