package com.murdock.guide.freemarker.basic.function;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

/**
 * <pre>
 * 模板工厂
 * </pre>
 *
 * @author weipeng2k 2021年06月04日 下午21:38:05
 */
public class TemplateFactory {

    private static final Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), "templates");
    }

    /**
     * 根据名称获取模板
     *
     * @param name 模板名称
     * @return 模板
     */
    public static Template getTemplate(String name) {
        try {
            return configuration.getTemplate(name);
        } catch (IOException ex) {
            throw new RuntimeException("get template error.", ex);
        }
    }
}
