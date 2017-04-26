package cn.zhanyongzhi.gmfs.store;

import cn.zhanyongzhi.gmfs.store.utils.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;

import static cn.zhanyongzhi.gmfs.store.utils.Utils.*;

public class Block {
    private static Logger logger = Logger.getLogger(Block.class);

    private static final long maxBlockSize = 64 * Utils.Size1G;
    private static final byte[] magicNum = "GMFS".getBytes();
    private static final int packSize = 8;


    private int blockId;
    private long checkSum;
    private byte[] data;

    public static Block loadFromFile(RandomAccessFile superBlockFile, int blockId) throws StoreException, IOException {
        Block block = new Block();
        block.setBlockId(blockId);

        int headInfoOffset = block.getHeaderInfoSize();
        long blockOffset = block.getBlockOffset(blockId);

        //load data length
        long dataLenOffset = blockOffset + headInfoOffset - 4;
        byte[] intByte = new byte[4];

        superBlockFile.seek(dataLenOffset);
        superBlockFile.read(intByte, 0, 4);

        int dataLen = byteArrayToInt(intByte);
        if(0 > dataLen || dataLen > maxBlockSize - blockOffset)
            throw new StoreException("data length invalid");

        byte[] data = new byte[dataLen];
        superBlockFile.read(data, 0, dataLen);

        byte[] checksumByte = new byte[8];
        superBlockFile.read(checksumByte, 0, 8);

        long checksum = byteArrayToLong(checksumByte);

        CRC32 crc32 = new CRC32();
        crc32.update(data, 0, dataLen);

        if(crc32.getValue() != checksum){
            throw new StoreException("data checksum invalid");
        }

        //set info
        block.setCheckSum(checksum);
        block.setData(data);


        return block;
    }

    public int writeToFile(FileChannel superBlockFileChannel) throws IOException {
        int writeSize = getWriteSize();
        int incSize = getPackIncSize();

        superBlockFileChannel.write(ByteBuffer.wrap(magicNum));
        superBlockFileChannel.write(ByteBuffer.wrap(intToByteArray(blockId)));
        superBlockFileChannel.write(ByteBuffer.wrap(intToByteArray(data.length)));

        superBlockFileChannel.write(ByteBuffer.wrap(data));

        superBlockFileChannel.write(ByteBuffer.wrap(longToByteArray(checkSum)));

        //按指定字节对齐
        if (0 != incSize)
            superBlockFileChannel.write(ByteBuffer.allocate(incSize));

        return writeSize + incSize;
    }

    public int getHeaderInfoSize(){
        int headerSize = magicNum.length + 4 + 4;
        return headerSize;
    }

    public int getWriteSize(){
        int writeSize = magicNum.length + 4 + 4 + data.length + 8;
        return writeSize;
    }

    public int getPackIncSize(){
        int incSize = (getWriteSize() + packSize - 1) % packSize;
        return incSize;
    }

    public int getBlockId(long writeByteCount){
        return (int) (writeByteCount / packSize);
    }

    public long getBlockOffset(int blockId){
        return packSize * blockId;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(long checkSum) {
        this.checkSum = checkSum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
