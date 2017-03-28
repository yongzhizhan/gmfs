package cn.zhanyongzhi.gmfs.store;

import struct.StructClass;
import struct.StructField;

/**
 * 数据块，用来作为最底层分块
 */
@StructClass
public class Block {
    @StructField(order = 0)
    public byte[] magicNumber = "GMFS".getBytes();

    @StructField(order = 1)
    public int dataLen;
}
