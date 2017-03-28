package cn.zhanyongzhi.gmfs.store;

import cn.zhanyongzhi.gmfs.store.leveldb.KeyValueDB;
import cn.zhanyongzhi.gmfs.store.utils.Utils;
import cn.zhanyongzhi.gmfs.store.utils.lock.AutoLocker;
import org.apache.log4j.Logger;
import struct.JavaStruct;
import struct.StructException;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32;

/**
 * 大量的小文件，会增加磁盘的负担，也增加directory索引的难度，通过使用超级块，磁盘索引的数量会大大减少，directory只需要
 * 维护好超级块的信息，就可以了，大大减少文件的数量。
 * 1. 当故障出现时，超级块会变为只读，需要外部命令
 * 2. 最大写入文件大小64M
 * 3. super block 的大小为2^32 * 8 字节
 */
public class SuperBlock implements Closeable {
    private static Logger logger = Logger.getLogger(SuperBlock.class.getName());
    private static int paddingSize = 8;

    private File superBlockFile;
    private OutputStream outputStream;
    private RandomAccessFile randomAccessFile;

    private int appendIndex = 0;
    private boolean isReadOnly = false;

    private KeyValueDB keyValueDB;

    public SuperBlock(File superBlockFile) throws IOException {
        this.superBlockFile = superBlockFile;

        keyValueDB = new KeyValueDB("test");

        outputStream = new FileOutputStream(superBlockFile, true);
        randomAccessFile = new RandomAccessFile(superBlockFile, "r");

        //File size
        long fileSize = superBlockFile.length();

        //calc the index, start at 0
        appendIndex = (int) (fileSize / paddingSize);

        //when the size is 32G,the superblock will became readonly
        if(32 * Utils.Size1G <= fileSize){
            isReadOnly = true;
        }

        //如果检查失败，就只读，保护数据
        if(false == isReadOnly)
            isReadOnly = (false == healthCheckBlock());
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        randomAccessFile.close();
        keyValueDB.close();
    }

    public boolean healthCheckBlock(){
        return true;
    }

    public long append(InputStream dataStream, int dataSize) throws StoreException {
        if(isReadOnly)
            throw new StoreException("super block is read only");

        Block block = new Block();
        block.dataLen = dataSize;

        int currentIndex;
        try(AutoLocker locker = new AutoLocker(new ReentrantLock())) {
            try {
                //write base info
                byte[] blockData = JavaStruct.pack(block);
                outputStream.write(blockData);

                byte[] appendBuffer = new byte[4096];
                int readSize = 0;

                CRC32 crc32 = new CRC32();

                while (-1 != (readSize = dataStream.read(appendBuffer, 0, appendBuffer.length))) {
                    outputStream.write(appendBuffer, 0, readSize);
                    crc32.update(appendBuffer, 0, readSize);
                }

                //get checksum, append checksum value
                long checksum = crc32.getValue();

                outputStream.write(Utils.longToByteArray(checksum));

                //padding block
                int skip = ((blockData.length + paddingSize - 1) / paddingSize) * paddingSize - blockData.length;
                for (int i = 0; i < skip; i++) {
                    outputStream.write((byte) 0xC);
                }

                currentIndex = appendIndex;
                appendIndex++;

                BlockIndex blockIndex = new BlockIndex();
                blockIndex.offset = currentIndex;
                blockIndex.dataLen = block.dataLen;
                blockIndex.checksum = checksum;

                keyValueDB.put(currentIndex, JavaStruct.pack(blockIndex));
            } catch (Exception e) {
                logger.error("append failed.", e);
                throw new StoreException("append failed.");
            }
        }

        return currentIndex;
    }

    private long getCheckSum(byte[] data){
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    public BlockReader getBlockReader(int key) throws StoreException {
        BlockIndex blockIndex = new BlockIndex();

        try(AutoLocker locker = new AutoLocker(new ReentrantLock())){
            byte[] data = keyValueDB.get(key);
            if(null == data)
                return null;

            JavaStruct.unpack(blockIndex, data);
            blockIndex = new BlockIndex();
        } catch (StructException e) {
            logger.error("struct parse failed.", e);
            throw new StoreException("struct parse failed.");
        }

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(superBlockFile);
        } catch (FileNotFoundException e) {
            logger.error("create input stream failed", e);
            throw new StoreException("create input stream failed");
        }

        return new BlockReader(blockIndex, inputStream);
    }

    public boolean remove(int key) throws StoreException {
        if(isReadOnly)
            return false;

        try(AutoLocker locker = new AutoLocker(new ReentrantLock())) {
            byte[] data = keyValueDB.get(key);
            if(null == data)
                return false;

            BlockIndex blockIndex = new BlockIndex();
            JavaStruct.unpack(blockIndex, data);
            blockIndex = new BlockIndex();

            keyValueDB.delete(key);

            //TODO:写入-1表示已经删除,4表示maginc number
            randomAccessFile.seek(blockIndex.offset + 4);
            randomAccessFile.write(Utils.intToByteArray(-1));
        } catch (StructException e) {
            logger.error("struct parse failed.", e);
            throw new StoreException("struct parse failed.");
        } catch (IOException e) {
            logger.error("delete file failed.");
            throw new StoreException("delete file failed.");
        }

        return true;
    }

    public boolean isReadonly(){
        return isReadOnly;
    }

    public void setReadOnly(boolean isReadOnly){
        this.isReadOnly = isReadOnly;
    }
}
