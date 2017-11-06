package com.alibaba.dubbo.registry.common;


import com.alibaba.dubbo.governance.sync.RegistryServerSync;
import com.alibaba.dubbo.registry.RegistryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yantingxin on 2017/6/25.
 */
public class RegistryManager implements Serializable {
    private static final long serialVersionUID = -7006167903423634464L;

    private ConcurrentHashMap<String, RegistryUnit> registryMap = new ConcurrentHashMap<String, RegistryUnit>(20);

    public void setRegistryUnitList(List<RegistryUnit> unitList) {
        if (CollectionUtils.isEmpty(unitList)) {
            return;
        }
        for (RegistryUnit unit : unitList) {
            registryMap.put(unit.getKey(), unit);
        }
    }

    public RegistryServerSync getRegistryServiceSync(String registryKey) {
        RegistryUnit registryUnit = registryMap.get(registryKey);
        if (registryUnit == null) {
            return null;
        }
        return registryUnit.getRegistryServerSync();
    }

    public RegistryService getRegistryService(String registryKey) {
        RegistryUnit registryUnit = registryMap.get(registryKey);
        if (registryUnit == null) {
            return null;
        }
        return registryUnit.getRegistryService();
    }
}
