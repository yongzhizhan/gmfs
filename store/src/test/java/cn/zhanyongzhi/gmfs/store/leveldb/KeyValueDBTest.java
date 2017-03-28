package cn.zhanyongzhi.gmfs.store.leveldb;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class KeyValueDBTest {
    @Test
    public void testDefault() throws IOException {
        try(KeyValueDB db = new KeyValueDB("test.db")) {
            db.put("foo".getBytes(), "bar".getBytes());

            byte[] value = db.get("foo".getBytes());

            System.out.printf(new String(value));
        }
    }

    @Test
    public void testIntKey() throws IOException {
        try(KeyValueDB db = new KeyValueDB("test.db")) {
            db.put(1, "test".getBytes());
            byte[] value = db.get(1);

            Assert.assertArrayEquals("test".getBytes(), value);
        }
    }
}