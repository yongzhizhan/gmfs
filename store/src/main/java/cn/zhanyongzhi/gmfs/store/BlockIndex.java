package cn.zhanyongzhi.gmfs.store;

import struct.StructClass;
import struct.StructField;

@StructClass
public class BlockIndex {
    @StructField(order = 0)
    public int offset;

    @StructField(order = 1)
    public int dataLen;

    @StructField(order = 2)
    public long checksum;
}
