package cn.zhanyongzhi.gmfs.store.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilsTest {
    @Test
    public void testConvert(){
        int foo = 0xC;
        byte[] intAry = Utils.intToByteArray(0xC);

        assertTrue(Arrays.equals(intAry, new byte[]{0, 0, 0, 0xC}));
        assertEquals(foo, Utils.byteArrayToInt(intAry, 0));

        long bar = 0x1122;
        byte[] longData = Utils.longToByteArray(bar);

        assertArrayEquals(new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x11, 0x22}, longData);
        assertEquals(bar, Utils.byteArrayToLong(longData));
    }

}