package cn.zhanyongzhi.gmfs.store;

import cn.zhanyongzhi.gmfs.store.utils.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;

public class SuperBlock implements Closeable {
    private static Logger logger = Logger.getLogger(SuperBlock.class);
    private static final long maxSuperBlockSize = 64 * Utils.Size1G;

    private int superBlockId;
    private int blockCount;
    private long writeByteCount;
    private boolean readOnly;
    private boolean opened;

    private RandomAccessFile superBlockFile;

    public SuperBlock(){
        readOnly = false;
        opened = false;
    }

    public boolean open(int superBlockId, File dataFile) throws IOException {
        if(true == isOpened())
            return false;

        this.superBlockId = superBlockId;
        superBlockFile = new RandomAccessFile(dataFile, "rw");
        blockCount = 0;
        writeByteCount = 0;

        //check file

        opened = true;
        return true;
    }

    public Block addBlock(byte[] data) throws StoreException {
        if(false == isOpened())
            throw new StoreException("file not opened");

        if(isReadOnly())
            throw new StoreException("file read only");

        Block block = new Block();
        block.setBlockId(block.getBlockId(writeByteCount));
        block.setData(data);

        //magic + blockid + datalen + data + checksum
        int writeSize = block.getWriteSize();
        int incSize = block.getPackIncSize();

        int totalWriteSize = writeSize + incSize;

        if(writeByteCount + totalWriteSize > data.length)
            throw new StoreException("super block size not enough");

        CRC32 crc32 = new CRC32();
        crc32.update(data);

        //upadate checksum
        block.setCheckSum(crc32.getValue());

        try {
            //not close
            FileChannel fileChannel = superBlockFile.getChannel();
            block.writeToFile(fileChannel);
        } catch (IOException e) {
            readOnly = true;

            logger.error("append failed", e);
            throw new StoreException("file append ioexception.");
        }

        writeByteCount += totalWriteSize;
        blockCount++;

        return block;
    }

    public Block read(int blockId) throws StoreException {
        if(false == isOpened())
            throw new StoreException("file not opened");

        Block block = new Block();
        try {
            block.loadFromFile(superBlockFile, blockId);
        }catch (Exception e){
            logger.error(String.format("read blockid:%d failed.", blockId), e);
            throw new StoreException("read block failed.");
        }

        return block;
    }

    public boolean delete(int blockId){
        return false;
    }

    @Override
    public void close() throws IOException {
        if(false == isOpened())
            return;

        superBlockFile.close();
        opened = false;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isOpened() {
        return opened;
    }
}
