package org.mightyfish.asn1.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.mightyfish.asn1.ASN1Boolean;
import org.mightyfish.asn1.ASN1Enumerated;
import org.mightyfish.asn1.ASN1InputStream;
import org.mightyfish.asn1.ASN1Integer;
import org.mightyfish.asn1.ASN1ObjectIdentifier;
import org.mightyfish.asn1.ASN1OutputStream;
import org.mightyfish.asn1.ASN1Primitive;
import org.mightyfish.asn1.BERConstructedOctetString;
import org.mightyfish.asn1.BERSequence;
import org.mightyfish.asn1.BERSet;
import org.mightyfish.asn1.BERTaggedObject;
import org.mightyfish.asn1.DERApplicationSpecific;
import org.mightyfish.asn1.DERBMPString;
import org.mightyfish.asn1.DERBitString;
import org.mightyfish.asn1.DERGeneralString;
import org.mightyfish.asn1.DERGeneralizedTime;
import org.mightyfish.asn1.DERIA5String;
import org.mightyfish.asn1.DERNull;
import org.mightyfish.asn1.DERNumericString;
import org.mightyfish.asn1.DEROctetString;
import org.mightyfish.asn1.DERPrintableString;
import org.mightyfish.asn1.DERSequence;
import org.mightyfish.asn1.DERSet;
import org.mightyfish.asn1.DERT61String;
import org.mightyfish.asn1.DERTaggedObject;
import org.mightyfish.asn1.DERUTCTime;
import org.mightyfish.asn1.DERUTF8String;
import org.mightyfish.asn1.DERUniversalString;
import org.mightyfish.asn1.DERVisibleString;
import org.mightyfish.util.test.SimpleTestResult;
import org.mightyfish.util.test.Test;
import org.mightyfish.util.test.TestResult;

public class EqualsAndHashCodeTest
    implements Test
{
    public TestResult perform()
    {
        byte[]    data = { 0, 1, 0, 1, 0, 0, 1 };
        
        ASN1Primitive    values[] = {
                new BERConstructedOctetString(data),
                new BERSequence(new DERPrintableString("hello world")),
                new BERSet(new DERPrintableString("hello world")),
                new BERTaggedObject(0, new DERPrintableString("hello world")),
                new DERApplicationSpecific(0, data),
                new DERBitString(data),
                new DERBMPString("hello world"),
                new ASN1Boolean(true),
                new ASN1Boolean(false),
                new ASN1Enumerated(100),
                new DERGeneralizedTime("20070315173729Z"),
                new DERGeneralString("hello world"),
                new DERIA5String("hello"),
                new ASN1Integer(1000),
                new DERNull(),
                new DERNumericString("123456"),
                new ASN1ObjectIdentifier("1.1.1.10000.1"),
                new DEROctetString(data),
                new DERPrintableString("hello world"),
                new DERSequence(new DERPrintableString("hello world")),
                new DERSet(new DERPrintableString("hello world")),
                new DERT61String("hello world"),
                new DERTaggedObject(0, new DERPrintableString("hello world")),
                new DERUniversalString(data),
                new DERUTCTime(new Date()),
                new DERUTF8String("hello world"),
                new DERVisibleString("hello world")
            };
        
        try
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            ASN1OutputStream        aOut = new ASN1OutputStream(bOut);
            
            for (int i = 0; i != values.length; i++)
            {
                aOut.writeObject(values[i]);
            }
            
            ASN1Primitive[] readValues = new ASN1Primitive[values.length];
            
            ByteArrayInputStream    bIn = new ByteArrayInputStream(bOut.toByteArray());
            ASN1InputStream         aIn = new ASN1InputStream(bIn);
            
            for (int i = 0; i != values.length; i++)
            {
                ASN1Primitive o = aIn.readObject();
                if (!o.equals(values[i]))
                {
                    return new SimpleTestResult(false, getName() + ": Failed equality test for " + o.getClass());
                }
                
                if (o.hashCode() != values[i].hashCode())
                {
                    return new SimpleTestResult(false, getName() + ": Failed hashCode test for " + o.getClass());
                }
            }
        }
        catch (Exception e)
        {
            return new SimpleTestResult(false, getName() + ": Failed - exception " + e.toString(), e);
        }
        
        return new SimpleTestResult(true, getName() + ": Okay");
    }

    public String getName()
    {
        return "EqualsAndHashCode";
    }

    public static void main(
        String[] args)
    {
        EqualsAndHashCodeTest    test = new EqualsAndHashCodeTest();
        TestResult      result = test.perform();

        System.out.println(result);
    }
}
