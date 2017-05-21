/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.config.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.config.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;

/**
 * ServiceFactoryBean
 * 
 * @author william.liangf
 * @export
 */
public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware {

	private static final long serialVersionUID = 213195494150089726L;

    private static transient ApplicationContext SPRING_CONTEXT;
    
	private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;
    
	public ServiceBean() {
        super();
    }

    public ServiceBean(Service service) {
        super(service);
    }

    public static ApplicationContext getSpringContext() {
	    return SPRING_CONTEXT;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		SpringExtensionFactory.addApplicationContext(applicationContext);
		if (applicationContext != null) {
		    SPRING_CONTEXT = applicationContext;
		    try {
	            Method method = applicationContext.getClass().getMethod("addApplicationListener", new Class<?>[]{ApplicationListener.class}); // 兼容Spring2.0.1
	            method.invoke(applicationContext, new Object[] {this});
	            supportedApplicationListener = true;
	        } catch (Throwable t) {
                if (applicationContext instanceof AbstractApplicationContext) {
    	            try {
    	                Method method = AbstractApplicationContext.class.getDeclaredMethod("addListener", new Class<?>[]{ApplicationListener.class}); // 兼容Spring2.0.1
                        if (! method.isAccessible()) {
                            method.setAccessible(true);
                        }
    	                method.invoke(applicationContext, new Object[] {this});
                        supportedApplicationListener = true;
    	            } catch (Throwable t2) {
    	            }
	            }
	        }
		}
	}

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (ContextRefreshedEvent.class.getName().equals(event.getClass().getName())) {
        	if (isDelay() && ! isExported() && ! isUnexported()) {
                if (logger.isInfoEnabled()) {
                    logger.info("The service ready on spring started. service: " + getInterface());
                }
                export();
            }
        }
    }
    
    private boolean isDelay() {
        Integer delay = getDelay();
        ProviderConfig provider = getProvider();
        if (delay == null && provider != null) {
            delay = provider.getDelay();
        }
        return supportedApplicationListener && (delay == null || delay.intValue() == -1);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	public void afterPropertiesSet() throws Exception {
        if (getProvider() == null) {
            Map<String, ProviderConfig> providerConfigMap = findSpringBeanMap(applicationContext, ProviderConfig.class);
            if (MapUtils.isNotEmpty(providerConfigMap)) {
                Map<String, ProtocolConfig> protocolConfigMap = findSpringBeanMap(applicationContext, ProtocolConfig.class);
                if (MapUtils.isEmpty(protocolConfigMap) && providerConfigMap.size() > 1) { // 兼容旧版本
                    List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() != null && config.isDefault()) {
                            providerConfigs.add(config);
                        }
                    }
                    if (providerConfigs.size() > 0) {
                        setProviders(providerConfigs);
                    }
                } else {
                    ProviderConfig providerConfig = findConfigBeanFromSpring(applicationContext, ProviderConfig.class);
                    if (providerConfig != null) {
                        setProvider(providerConfig);
                    }
                }
            }
        }
        if (getApplication() == null
                && (getProvider() == null || getProvider().getApplication() == null)) {
            ApplicationConfig applicationConfig = findConfigBeanFromSpring(applicationContext, ApplicationConfig.class);

            if (applicationConfig != null) {
                setApplication(applicationConfig);
            }
        }
        if (getModule() == null
                && (getProvider() == null || getProvider().getModule() == null)) {
            ModuleConfig moduleConfig = findConfigBeanFromSpring(applicationContext, ModuleConfig.class);
            if (moduleConfig != null) {
                setModule(moduleConfig);
            }

        }
        if ((getRegistries() == null || getRegistries().size() == 0)
                && (getProvider() == null || getProvider().getRegistries() == null || getProvider().getRegistries().size() == 0)
                && (getApplication() == null || getApplication().getRegistries() == null || getApplication().getRegistries().size() == 0)) {

            List<RegistryConfig> registryConfigs = findConfigListBeanFromSpring(applicationContext, RegistryConfig.class);
            if (CollectionUtils.isNotEmpty(registryConfigs)) {
                super.setRegistries(registryConfigs);
            }
        }
        if (getMonitor() == null
                && (getProvider() == null || getProvider().getMonitor() == null)
                && (getApplication() == null || getApplication().getMonitor() == null)) {
            MonitorConfig monitorConfig = findConfigBeanFromSpring(applicationContext, MonitorConfig.class);
            if (monitorConfig != null) {
                setMonitor(monitorConfig);
            }
        }
        if ((getProtocols() == null || getProtocols().size() == 0)
                && (getProvider() == null || getProvider().getProtocols() == null || getProvider().getProtocols().size() == 0)) {
            List<ProtocolConfig> protocolConfigs = findConfigListBeanFromSpring(applicationContext, ProtocolConfig.class);
            if (CollectionUtils.isNotEmpty(protocolConfigs)) {
                super.setProtocols(protocolConfigs);
            }
        }
        if (getPath() == null || getPath().length() == 0) {
            if (beanName != null && beanName.length() > 0 
                    && getInterface() != null && getInterface().length() > 0
                    && beanName.startsWith(getInterface())) {
                setPath(beanName);
            }
        }
        if (! isDelay()) {
            export();
        }
    }

    private <T extends AbstractConfig> T findConfigBeanFromSpring(ApplicationContext applicationContext, Class<T> clazz) {
        Map<String, T> configMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, clazz, false, false);
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }
        Method method = getIsDefaultMethod(clazz);
        if (method == null) {
            return null;
        }

        T targetConfig = null;
        for (T config : configMap.values()) {
            Boolean isDefault = invokeIsDefault(method, config);
            if (isDefault == null || isDefault) {
                if (targetConfig != null) {
                    throw new IllegalStateException("Duplicate " + clazz.getSimpleName() + " configs: " + targetConfig + " and " + config);
                }
                targetConfig = config;
            }
        }
        return targetConfig;
    }

    private <T extends AbstractConfig> List<T> findConfigListBeanFromSpring(ApplicationContext applicationContext, Class<T> clazz) {
        Map<String, T> configMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, clazz, false, false);
        if (MapUtils.isEmpty(configMap)) {
            return null;
        }

        Method method = getIsDefaultMethod(clazz);
        if (method == null) {
            return null;
        }

        List<T> configs = new ArrayList<T>(6);
        for (T config : configMap.values()) {
            Boolean isDefault = invokeIsDefault(method, config);
            if (isDefault == null || isDefault) {
                configs.add(config);
            }
        }
        return configs;
    }

    private <T> Map<String, T> findSpringBeanMap(ApplicationContext applicationContext, Class<T> clazz) {
        return applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, clazz, false, false);
    }

    private <T extends AbstractConfig> Method getIsDefaultMethod(Class<T> clazz) {
        try {
            return ReflectUtils.findMethodByMethodName(clazz, "isDefault");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean invokeIsDefault(Method method, AbstractConfig config) {
        Boolean isDefault = null;
        try {
            isDefault = (Boolean) method.invoke(config);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isDefault;
    }

    public void destroy() throws Exception {
        unexport();
    }

}