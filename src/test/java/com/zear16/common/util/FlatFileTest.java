package com.zear16.common.util;

import com.zear16.common.util.model.*;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;

public class FlatFileTest {

    @Test(expected = InvalidParameterException.class)
    public void invalidObjectType() throws Exception {
        FlatFile flatFile = new FlatFile(Success.class);
        InvalidLength obj = new InvalidLength();
        flatFile.toString(obj);
    }

    @Test
    public void success() throws Exception {
        FlatFile parser = new FlatFile(Success.class);
        Success success = parser.parseLine(
                "01104510051068690017034994200764001801002889040120180025285199" +
                        "XXXXXXXX5781XXXXXXXXXXX00000004800000136752" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "00000000000000038524" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "Booking Ticket" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "XXXXXXXXXXXXXXXX" +
                        "1.46.39.177XXXXXXXRTHE HONGKONG AND SHANGHAI BANKING CORPORATION LIMITED" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "HONG KONG" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                        "000230303030303030303030303032343637383435358C38B2CECDFB26E5D30810000020FB0449000000");
        assertEquals(success.getDate(), "04012018");
        assertEquals(success.getInvoiceNo(), "001801002889");
        assertEquals(success.getMerchantId(), "451005106869001");
        assertEquals(success.getResponseCode(), "00");
        assertEquals(success.getTransAmount(), "000000048000");
    }

    @Test
    public void alignRight() throws Exception {
        FlatFile flatFile = new FlatFile(AlignRight.class);
        AlignRight obj = new AlignRight();
        obj.setField("12345678");
        String s = flatFile.toString(obj);
        assertEquals("  12345678", s);
    }

    @Test
    public void alignLeft() throws Exception {
        FlatFile flatFile = new FlatFile(AlignLeft.class);
        AlignLeft obj = new AlignLeft();
        obj.setField("12345678");
        String s = flatFile.toString(obj);
        assertEquals("12345678  ", s);
    }

    @Test
    public void paddingWithCharacterX() throws Exception {
        FlatFile flatFile = new FlatFile(PaddingX.class);
        PaddingX obj = new PaddingX();
        obj.setField("12345678");
        String s = flatFile.toString(obj);
        assertEquals("12345678XX", s);
    }

    @Test
    public void paddingLine() throws Exception {
        FlatFile flatFile = new FlatFile(RecordPadding.class);
        RecordPadding obj = new RecordPadding();
        obj.setField1("01234567890123456789");
        obj.setField2("01234567890123456789012345678901234567890123456789");
        String s = flatFile.toString(obj);
        assertEquals(
                "          01234567890123456789                                                                      01234567890123456789012345678901234567890123456789                                                                                                         ",
                s);
    }

    @Test(expected = InvalidParameterException.class)
    public void offsetLengthOverflow() throws Exception {
        new FlatFile(OffsetLengthOverflow.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void getterNotFound() throws Exception {
        new FlatFile(GetterNotFound.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void setterNotFound() throws Exception {
        new FlatFile(SetterNotFound.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void invalidSetterMethod() throws Exception {
        new FlatFile(InvalidSetter.class);
    }

    @Test(expected = InvalidParameterException.class)
    public void offsetMustMoreThanOrEqualZero() throws Exception {
        new FlatFile(InvalidOffset.class);
    }

    @Test(expected = InvalidParameterException.class)
    public void lengthMustMoreThanZero() throws Exception {
        new FlatFile(InvalidLength.class);
    }

    @Test(expected = InvalidParameterException.class)
    public void offsetOverlapped() throws Exception {
        new FlatFile(OverlappedOffset.class);
    }
}
