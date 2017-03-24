package cn.zhanyongzhi.gmfs.store;

import cn.zhanyongzhi.gmfs.dubbointerface.IStore;

public class Store implements IStore {
    @Override
    public boolean append(long fileId, byte[] data) {
        return false;
    }

    @Override
    public byte[] read(long fileId, long offset, int size) {
        return new byte[0];
    }

    @Override
    public boolean remove(long fileId) {
        return false;
    }

    @Override
    public void compact() {

    }

    @Override
    public String stat() {
        return null;
    }
}
