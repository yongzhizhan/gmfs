package cn.zhanyongzhi.gmfs.store.leveldb;

import cn.zhanyongzhi.gmfs.store.utils.Utils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

public class KeyValueDB implements Closeable{
    DB levelDB;

    public KeyValueDB(String filePath) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        levelDB = factory.open(new File(filePath), options);
    }

    public void put(byte[] key, byte[] value){
        levelDB.put(key, value);
    }

    public void put(String key, byte[] value){
        put(key.getBytes(), value);
    }

    public void put(int key, byte[] value){
        put(Utils.intToByteArray(key), value);
    }

    public void put(long key, byte[] value){
        put(Utils.longToByteArray(key), value);
    }

    public byte[] get(byte[] key){
        return levelDB.get(key);
    }

    public byte[] get(String key){
        return levelDB.get(key.getBytes());
    }

    public byte[] get(int key){
        return levelDB.get(Utils.intToByteArray(key));
    }

    public byte[] get(long key){
        return levelDB.get(Utils.longToByteArray(key));
    }

    public void delete(byte[] key){
        levelDB.delete(key);
    }

    public void delete(String key){
        levelDB.delete(key.getBytes());
    }

    public void delete(int key){
        levelDB.delete(Utils.intToByteArray(key));
    }

    public void delete(long key){
        levelDB.delete(Utils.longToByteArray(key));
    }

    @Override
    public void close() throws IOException {
        levelDB.close();
    }
}
