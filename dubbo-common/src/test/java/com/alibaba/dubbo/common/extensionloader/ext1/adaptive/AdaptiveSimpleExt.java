package com.alibaba.dubbo.common.extensionloader.ext1.adaptive;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt;

/**
 * Created by yantingxin on 2017/6/11.
 * 测试类adaptive的作用，在生成相应adaptive代码的时候，可以指定自己的类来代替动态生成的类
 */
@Adaptive
public class AdaptiveSimpleExt implements SimpleExt {
    public String echo(URL url, String s) {
        return "AdaptiveSimpleExt echo";
    }

    public String yell(URL url, String s) {
        return "AdaptiveSimpleExt yell";
    }

    public String bang(URL url, int i) {
        return "AdaptiveSimpleExt bnag";
    }
}
