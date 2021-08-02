package com.murdock.guide.freemarker.spring.integration.impl;

import com.murdock.guide.freemarker.spring.integration.TemplateFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

/**
 * @author weipeng2k 2021年06月10日 下午18:12:32
 */
public class TemplateFactoryImpl implements TemplateFactory, InitializingBean {

    private Configuration configuration;
    /**
     * 前缀目录
     */
    private String prefixDir = "templates";
    /**
     * 编码
     */
    private String encodingCharSet = "UTF-8";

    @Override
    public Template getTemplate(String name) {
        try {
            return configuration.getTemplate(name);
        } catch (IOException ex) {
            throw new RuntimeException("TemplateFactory get template " + name + "got error", ex);
        }
    }

    public void setPrefixDir(String prefixDir) {
        this.prefixDir = prefixDir;
    }

    public void setEncodingCharSet(String encodingCharSet) {
        this.encodingCharSet = encodingCharSet;
    }

    @Override
    public void afterPropertiesSet() {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), prefixDir);
        configuration.setDefaultEncoding(encodingCharSet);
    }
}
