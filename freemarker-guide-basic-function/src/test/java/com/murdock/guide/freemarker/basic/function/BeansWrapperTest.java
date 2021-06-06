package com.murdock.guide.freemarker.basic.function;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.StringModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.junit.Assert;
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

        System.out.println(((StringModel) templateModel).getAsString());
    }

    @Test
    public void wrapper2() throws Exception {
        Configuration configuration = TemplateFactory.getConfiguration();
        ObjectWrapper objectWrapper = configuration.getObjectWrapper();

        User user = new User();
        user.setAge(18);
        user.setName("流川枫");
        user.setSex("男");
        Phone phone = new Phone();
        phone.setBrand("Apple");
        phone.setModel("iPhone 12 Pro Max");
        user.setPhone(phone);

        TemplateModel wrap = objectWrapper.wrap(user);
        StringModel stringModel = (StringModel) wrap;
        TemplateModel age = stringModel.get("age");
        SimpleNumber simpleNumber = (SimpleNumber) age;
        Assert.assertEquals(18, simpleNumber.getAsNumber().intValue());

        TemplateModel templateModel = stringModel.get("phone");
        StringModel sm = (StringModel) templateModel;
        TemplateModel brand = sm.get("brand");
        SimpleScalar sm2 = (SimpleScalar) brand;
        Assert.assertEquals("Apple", sm2.getAsString());
    }

}
