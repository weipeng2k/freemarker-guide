package com.murdock.guide.freemarker.basic.function;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.StringModel;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.junit.Test;

/**
 * @author weipeng2k 2021年06月05日 下午20:46:33
 */
public class BeansWrapperTest {

    @Test
    public void wrapper() throws TemplateModelException {
        BeansWrapperBuilder builder = new BeansWrapperBuilder(Configuration.VERSION_2_3_31);
        builder.setUseModelCache(true);
        builder.setExposeFields(true);
        BeansWrapper beansWrapper = builder.build();

        User user = new User();
        user.setAge(18);
        user.setName("流川枫");
        user.setSex("男");
        Phone phone = new Phone();
        phone.setBrand("Apple");
        phone.setModel("iPhone 12 Pro Max");
        user.setPhone(phone);

        TemplateModel templateModel = beansWrapper.wrap(user);

        System.out.println(((StringModel)templateModel).getAsString());

    }
}
