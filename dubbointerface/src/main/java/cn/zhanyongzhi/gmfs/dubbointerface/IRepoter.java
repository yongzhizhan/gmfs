package cn.zhanyongzhi.gmfs.dubbointerface;

import cn.zhanyongzhi.gmfs.dubbointerface.model.StoreStatus;

import java.util.List;

public interface IRepoter {
    List<StoreStatus> getStoreStatus();
}
