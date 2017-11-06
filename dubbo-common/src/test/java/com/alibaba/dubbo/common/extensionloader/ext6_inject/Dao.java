package com.alibaba.dubbo.common.extensionloader.ext6_inject;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

@SPI("impl")
public interface Dao {
    @Adaptive
    public void update();
}
