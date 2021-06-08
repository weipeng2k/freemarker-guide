## 使用入门

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FreeMarker同Velocity一样，都属于模板引擎技术，在SpringBoot微服务中，使用它们做页面模板渲染。由于公司技术栈的原因，笔者常用的是Velocity模板，但是在新的SpringBoot版本中，Velocity的starter已经不再推荐，转为FreeMarker。

> Velocity万年不更新，但是一样老的FreeMarker更新还是比较频繁的

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;模板引擎技术不少，比如号称更好的展示标签，用浏览器方便预览的thymeleaf模板引擎。但在IDE已经支持很好的旧有模板引擎技术，比如：FreeMarker和Velocity，这显得意义不大，而且低劣的性能让人觉得没有多少存在意义。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FreeMarker性能中规中矩（基本也4-5倍于thymeleaf），老而弥坚，还是值得学习一下。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面的示例可以在[https://github.com/weipeng2k/freemarker-guide](https://github.com/weipeng2k/freemarker-guide)找到，不仅如此，本文中出现的代码示例都可以找到。

### 一个简单示例

> 在`freemarker-guide/freemarker-guide-basic-function`项目中。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先添加依赖：

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选择的版本是：2.3.31。在`resources/templates`目录下创建一个`test.ftl`模板文件，内容是：`hello, ${name}!`，如下图：

![test.ftl](https://upload-images.jianshu.io/upload_images/6205088-18b188db2102f57a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到`test.ftl`有一个`${}`描述符，其中的内容是：`name`。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;有了模板文件，相当于有了展示层，我们在构造数据，将数据“放置”到模板上，就可以“渲染”出我们预期的内容了。

```java
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
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述代码完成了模板的载入，数据的创建，以及模板和数据的合并渲染。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;观察上述代码，FreeMarker需要先创建一个`Configuration`，通过它获取`Template`，这里也就是之前创建的`test.ftl`。然后构造一个数据模型，这里是`HashMap`，最后通过`Template#process`方法，将数据和模板合并，并将内容输出到指定的输出流中。

### 需要了解的概念

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;模板引擎是用来混合数据和模板来得到输出的内容。

![Concept](https://upload-images.jianshu.io/upload_images/6205088-d68406d4bd9afb3b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如上图描述，模板引擎（FreeMarkder）在概念上主要包括：模板、数据和输出，相信任何模板引擎都会对这三者进行抽象和定义。模板，需要建立一个抽象能够定义和描述模板中的元素。数据，需要建立一个能够匹配模板中表达式的类型系统。输出，需要建立模板与数据合并后，最终按照何种字符集将内容写到输出中。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面会围绕这三个概念，介绍FreeMarker是如何兑现它们的。

## Configuration配置

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`Configuration`，顾名思义，是FreeMarker用来存储配置的，或者说是配置的抽象。FreeMarker将全局配置，比如：模板的位置，共享变量，这些都放置在Configuration中，可以说，它是有状态的，且是全局的。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`Configuration`推荐是一个共享的单例，它将引用暴露出来，让使用者可以用它去创建（或者获取）模板，所以说，它除了是配置，还是FreeMarker编程界面的入口。

### 配置作用

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`Configuration`用来保存配置和共享变量，在前面的示例代码中：

```java
Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
configuration.setDefaultEncoding("UTF-8");
configuration.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), "templates");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;它设置了版本，编码格式以及模板加载的位置。从方法也可以看出来，这些设置是全局的，它们会影响到FreeMarker对模板加载，合并以及输出的过程。

### TemplateFactory

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;由于需要通过`Configuration`获取模板`Template`，所以对于要求单例化的`Configuration`可以通过封装到`TemplateFactory`中加以简化。

```java
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
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过`TemplateFactory`可以简化模板的获取以及使用，如下面代码所示：

```java
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
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`Configuration`在创建时，需要设置版本`Version`，这个点有点意思。它是解决在CLASSPATH下，存在多个FreeMarker版本的问题，使用者设置了一个版本A，结果发现加载的是另一个版本，会报错。这种检测方式感觉有些历史，实际可以通过使用`ClassLoader#getResources`去加载Jar中一些固定存在的类，如果发现多份（亦或版本不兼容，这个显得高级点），就会报错。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;按照道理Maven仲裁可以解决这类问题，毕竟会仲裁出一个版本，而这么做的原因，很大程度上可能是FreeMarker的坐标有变动？这个没有更深入求证，意义不大，至于版本要求，按照自己依赖的版本设置即可，不必太纠结。

## Template模板

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过`TemplateFactory`获取到`Template`后，就可以使用该实例，合并数据，渲染出内容。

### 合并处理

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

### 

## TemplateModel数据模型


