package cn.zhanyongzhi.gmfs.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SuperBlockTest {
    SuperBlock superBlock;

    @Before
    public void setup() throws IOException {
        File superBlockFile = new File("super.block");
        superBlockFile.delete();
        superBlockFile.createNewFile();

        superBlock = new SuperBlock(superBlockFile);
    }

    @After
    public void tearDown() throws IOException {
        superBlock.close();
    }

    @Test
    public void testDefault() throws IOException, StoreException {
        byte[] data = "1".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        superBlock.append(inputStream, data.length);
    }

    @Test
    public void test1KBlock(){
        //
    }
}