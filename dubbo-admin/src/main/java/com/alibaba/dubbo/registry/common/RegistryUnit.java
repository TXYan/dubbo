package com.alibaba.dubbo.registry.common;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.governance.sync.RegistryServerSync;
import com.alibaba.dubbo.registry.RegistryService;

import java.io.Serializable;

/**
 * Created by yantingxin on 2017/6/27.
 */
public class RegistryUnit implements Serializable {
    private static final long serialVersionUID = -7457471534089758017L;

    private String name;

    private String key;

    private RegistryConfig registryConfig;

    private RegistryService registryService;

    private RegistryServerSync registryServerSync;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public RegistryServerSync getRegistryServerSync() {
        return registryServerSync;
    }

    public void setRegistryServerSync(RegistryServerSync registryServerSync) {
        this.registryServerSync = registryServerSync;
    }
}
