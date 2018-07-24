/*
 * Copyright 2013-2018 imadcn Group.
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
package com.imadcn.framework.idworker.spring.schema.parser;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.imadcn.framework.idworker.common.GeneratorStrategy;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.generator.CompressUUIDGenerator;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;
import com.imadcn.framework.idworker.spring.common.GeneratorBeanDefinitionTag;

public abstract class GeneratorRegisteryBuilder extends BaseBeanDefinitionParser {

	/**
	 * snowflake策略：zookeeper依赖配置
	 * @param element
	 * @param parserContext
	 * @return
	 */
	public static AbstractBeanDefinition buildWorkerNodeRegisterBeanDefinition(final Element element, final ParserContext parserContext) {
		 BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperWorkerRegister.class);
		 result.addConstructorArgReference(element.getAttribute(GeneratorBeanDefinitionTag.REGISTRY_CENTER_REF));
		 result.addConstructorArgValue(buildApplicationConfigurationBeanDefinition(element, parserContext));
		 return result.getBeanDefinition();
	} 
    
    /**
     * snowflake策略：app config 参数
     * @param element
     * @param parserContext
     * @return
     */
    public static AbstractBeanDefinition buildApplicationConfigurationBeanDefinition(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(ApplicationConfiguration.class);
        addPropertyValueIfNotEmpty(GeneratorBeanDefinitionTag.GROUOP, "group", element, configuration);
        addPropertyValueIfNotEmpty(GeneratorBeanDefinitionTag.STRATEGY, "strategy", element, configuration);
        return configuration.getBeanDefinition();
    }
    
    /**
     * 获取ID生成策略 Class
     * @param strategyCode
     * @return
     */
    public static Class<?> getGeneratorClass(final Element element) {
    	String strategyCode = getAttributeValue(element, GeneratorBeanDefinitionTag.STRATEGY);
    	GeneratorStrategy strategy = GeneratorStrategy.getByCode(strategyCode);
    	if (strategy == null) {
    		throw new IllegalArgumentException("unsupported generator strategy.");
    	}
    	switch (strategy) {
    	case SNOWFLAKE:
    		return SnowflakeGenerator.class;
    	case COMPRESS_UUID:
    		return CompressUUIDGenerator.class;
    	default:
    		return SnowflakeGenerator.class;
    	}
    }
}