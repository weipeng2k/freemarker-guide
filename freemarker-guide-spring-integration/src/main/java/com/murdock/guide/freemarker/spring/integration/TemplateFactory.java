package com.murdock.guide.freemarker.spring.integration;

import freemarker.template.Template;

/**
 * <pre>
 * 获取Template的工厂
 * </pre>
 *
 * @author weipeng2k 2021年06月10日 下午18:11:11
 */
public interface TemplateFactory {

    /**
     * 根据模板名称获取模板
     *
     * @param name 名称
     * @return 模板
     */
    Template getTemplate(String name);
}
