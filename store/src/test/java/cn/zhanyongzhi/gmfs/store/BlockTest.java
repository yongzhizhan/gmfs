package cn.zhanyongzhi.gmfs.store;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

import static org.testng.Assert.*;

public class BlockTest {
    @Test
    public void testDefault() throws IOException, StoreException {
        RandomAccessFile superBlockFile = new RandomAccessFile(new File("superblock"), "rw");

        Block block = new Block();

        int blockId = block.getBlockId(0);

        block.setBlockId(blockId);

        byte[] data = "123456".getBytes();
        block.setData(data);

        CRC32 crc32 = new CRC32();
        crc32.update(data);

        long checksum = crc32.getValue();

        block.setCheckSum(checksum);

        block.writeToFile(superBlockFile.getChannel());

        Block blockRead = Block.loadFromFile(superBlockFile, blockId);

        assertEquals(blockRead.getBlockId(), 0);
        assertEquals(blockRead.getCheckSum(), checksum);
        assertEquals(blockRead.getData(), data);
    }
}