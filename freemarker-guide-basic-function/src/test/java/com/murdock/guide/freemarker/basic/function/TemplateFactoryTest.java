package com.murdock.guide.freemarker.basic.function;

import freemarker.template.Template;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author weipeng2k 2021年06月04日 下午21:46:38
 */
public class TemplateFactoryTest {

    @Test(expected = RuntimeException.class)
    public void getTemplateNone() {
        TemplateFactory.getTemplate("none.ftl");
    }

    @Test
    public void getTemplate() {
        Template template = TemplateFactory.getTemplate("test.ftl");
        String sourceName = template.getSourceName();
        System.out.println("SourceName:" + sourceName);
        System.out.println(template);
        assertNotNull(template);
    }
}