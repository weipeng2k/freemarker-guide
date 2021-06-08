package com.murdock.guide.freemarker.basic.function;

import freemarker.core.TemplateElement;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author weipeng2k 2021年06月05日 下午21:48:53
 */
public class TemplateTest {

    @Test
    public void render() throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), "templates");

        Template template = configuration.getTemplate("test.ftl");

        Map<String, Object> model = new HashMap<>();
        model.put("name", "world");

        Writer output = new StringWriter();
        template.process(model, output);

        output.flush();
        System.out.println(output);
        assertEquals("hello, world!", output.toString());
    }

    @Test
    public void render2() throws Exception {
        Template template = TemplateFactory.getTemplate("test.ftl");

        Map<String, Object> model = new HashMap<>();
        model.put("name", "world");

        Writer output = new StringWriter();
        template.process(model, output);

        output.flush();
        System.out.println(output);
        assertEquals("hello, world!", output.toString());
    }

    @Test
    public void pojoRender() throws Exception {
        Template template = TemplateFactory.getTemplate("pojo-test.ftl");

        User user = new User();
        user.setAge(18);
        user.setName("流川枫");
        user.setSex("男");
        Phone phone = new Phone();
        phone.setBrand("Apple");
        phone.setModel("iPhone 12 Pro Max");
        user.setPhone(phone);

        Writer output = new StringWriter();
        template.process(user, output);

        output.flush();
        System.out.println(output);

        assertTrue(output.toString().contains("Max"));
    }

    @Test
    public void node() {
        Template template = TemplateFactory.getTemplate("pojo-test.ftl");
        TemplateElement rootTreeNode = template.getRootTreeNode();
        travelElement(rootTreeNode, 0);
    }

    public void travelElement(TemplateElement templateElement, int escape) {
        if (!templateElement.isLeaf()) {
            System.out.println(minusStr(escape) + templateElement + "@" + templateElement.getClass().getName());
            Iterator iterator = templateElement.children().asIterator();
            while (iterator.hasNext()) {
                travelElement((TemplateElement) iterator.next(), escape + 2);
            }
        } else {
            System.out.println(minusStr(escape) + templateElement + "@" + templateElement.getClass().getName());
        }
    }

    private static String minusStr(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

}
