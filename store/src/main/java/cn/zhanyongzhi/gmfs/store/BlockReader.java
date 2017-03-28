package cn.zhanyongzhi.gmfs.store;

import java.io.IOException;
import java.io.InputStream;

public class BlockReader {
    private InputStream dataStream;
    private BlockIndex blockIndex;

    private int readSize = 0;

    public BlockReader(BlockIndex blockIndex, InputStream dataStream) throws StoreException {
        this.blockIndex = blockIndex;
        this.dataStream = dataStream;
    }

    public int getDataLen(){
        return blockIndex.dataLen;
    }

    public synchronized int readData(byte[] data, int offset, int size) throws IOException {
        if(readSize == blockIndex.dataLen)
            return -1;

        if(readSize + size > blockIndex.dataLen)
            size = blockIndex.dataLen - readSize;

        int curSize = dataStream.read(data, offset, size);
        readSize += curSize;

        return curSize;
    }

    public boolean isValid(long crc32){
        return (crc32 == blockIndex.checksum);
    }
}
