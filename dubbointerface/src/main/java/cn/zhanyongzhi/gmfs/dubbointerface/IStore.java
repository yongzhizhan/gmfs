package cn.zhanyongzhi.gmfs.dubbointerface;

public interface IStore {
    boolean append(long fileId, byte[] data);

    byte[] read(long fileId, long offset, int size);

    boolean remove(long fileId);

    void compact();

    String stat();
}
