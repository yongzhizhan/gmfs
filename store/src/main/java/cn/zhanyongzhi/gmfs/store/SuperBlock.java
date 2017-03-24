package cn.zhanyongzhi.gmfs.store;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

/**
 * 大量的小文件，会增加磁盘的负担，也增加directory索引的难度，通过使用超级快，磁盘索引的数量会大大减少，directory只需要
 * 维护好超级块的信息，就可以了，大大减少文件的数量。
 */
public class SuperBlock {
    private static Logger logger = Logger.getLogger(SuperBlock.class.getName());

    private int blockId;
    private File superBlockFile;
    private boolean isReadOnly;
    private AtomicInteger appendIndex;

    public SuperBlock(File superBlockFile, int blockId){
        this.superBlockFile = superBlockFile;
        this.blockId = blockId;

        isReadOnly = true;
    }

    /**
     * 追加数据返回块索引
     * @param data 需要保存的数据
     * @return 数据块索引
     */
    public long append(byte[] data) throws IOException {
        Block block = new Block();
        block.setData(data);
        block.setSize(data.length);

        CRC32 crc32 = new CRC32();
        crc32.update(data);
        block.setChecksum(crc32.getValue());

        try {
            OutputStream outputStream = new FileOutputStream(superBlockFile);
            IOUtils.write(block.toBinary(), outputStream);
        }catch (IOException e){
            isReadOnly = true;
            throw e;
        }

        return appendIndex.incrementAndGet();
    }

    /**
     * 获取块信息
     * @param index 通过索引获取
     * @return 数据块信息
     */
    public Block getBlock(long index){
        long pos = index * Block.getPackSize();
        Block block = null;

        try {
            RandomAccessFile randomAccess = new RandomAccessFile(superBlockFile, "r");
            randomAccess.seek(pos);

            byte[] header = new byte[Block.getHeaderSize()];
            randomAccess.readFully(header);

            int dataSize = Block.getDataSize(header);

            byte[] data = new byte[dataSize];
            randomAccess.readFully(data);

            block = Block.fromBinary(header, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return block;
    }

    /**
     * 删除块信息
     * @param index 数据块索引
     * @return 是否成功
     */
    public boolean remove(int index){
        return false;
    }

    /**
     * 判断超级快是否为只读，在IO异常、磁盘满这些情况下，会进入只读状态，需要人工干预修正
     * @return 是否为只读状态
     */
    public boolean isReadonly(){
        return isReadOnly;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}
