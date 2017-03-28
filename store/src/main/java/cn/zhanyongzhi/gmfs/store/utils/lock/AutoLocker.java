package cn.zhanyongzhi.gmfs.store.utils.lock;

import java.io.Closeable;
import java.util.concurrent.locks.ReentrantLock;

public class AutoLocker implements Closeable {
    private ReentrantLock lock;

    public AutoLocker(ReentrantLock lock){
        this.lock = lock;

        lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }
}
