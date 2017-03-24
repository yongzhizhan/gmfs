package cn.zhanyongzhi.gmfs.dubbointerface;

import cn.zhanyongzhi.gmfs.dubbointerface.model.StoreInfo;

import java.util.List;

public interface IDirectory {
    StoreInfo upload(long requireSize);

    List<StoreInfo> read(long fileId);

    StoreInfo remove(long fileId);

    String stat();
}
