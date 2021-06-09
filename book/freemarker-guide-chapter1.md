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

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过`Template#process`方法，完成模板与数据的合并渲染工作。

```java
Map<String, Object> model = new HashMap<>();
model.put("name", "world");

Writer output = new StringWriter();
template.process(model, output);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`process(Object dataModel, Writer out) throws TemplateException, IOException`方法，接受一个数据模型Object，以及输出流Writer，后者可以简单的认为通过合并后的数据将会输出到流中，而前者将会被FreeMarker包装为数据模型，方便后续模板元素`TemplateElement`进行访问。

### TemplateElement

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`FreeMarker`将ftl模板文件进行了抽象，如下图所示：

![TemplateElement](https://upload-images.jianshu.io/upload_images/6205088-66279fce322d4dd9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一个`hello, ${name}!`在`FreeMarker`中被抽象成了一组结构，一个`MixedContent`包裹着两个`TextBlock`和一个`DollarVariable`，他们各自对应着文本和变量，它们的超类都是`TemplateElement`。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以想象，当合并渲染时，传入的是`TemplateElement`根（元素）节点，它会递归的遍历下面的所有元素，然后进行适当的处理，比如：`TextBlock`就直接输出，而`DollarVariable`则会同数据模型进行合并，生成出内容后再输出。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`TemplateElement`对应与模板文件的抽象，它将模板文件上的元素和Java代码连接起来，是个很不错的抽象。

## TemplateModel数据模型

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在进行模板输出时，传入的数据类型是`Object`，但是却能被模板的占位符正确解析。最简的做法，就是不使用数据模型，通过反射进行获取，比如：`${name}`，就通过JAVA反射获取`name`字段加以显示。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这么做看似能解决一些问题，但是问题场景一旦复杂起来，就无法解决了，比如：占位符中的内容是一个列表，我们需要进行遍历，反射就很难妥帖的处理这个场景了。

### TemlateModel

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`FreeMarker`将数据模型统一的定义为`TemplateModel`，通过其子类（和抽象体系）来完成对任意数据结构的定义。这么说有些抽象，我们先看两个数据结构。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Phone定义：

```java
@Getter
@Setter
@ToString
public class Phone {
    private String brand;

    private String model;
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User定义：

```java
@Getter
@Setter
@ToString
public class User {

    private String name;

    private String sex;

    private int age;

    private Phone phone;
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到只是一个一对一的结构。可以通过创建一个`User`对象，来看一下`FreeMarker`如何包装它。

```java
@Test
public void wrapper() throws TemplateModelException {
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

    TemplateModel templateModel = objectWrapper.wrap(user);

    StringModel stringModel = (StringModel) templateModel;

    TemplateModel age = stringModel.get("age");
    System.out.println("int ==> " + age.getClass().getName());

    TemplateModel sex = stringModel.get("sex");
    System.out.println("String ==> " + sex.getClass().getName());

    TemplateModel phone1 = stringModel.get("phone");
    System.out.println("Phone ==> " + phone1.getClass().getName());
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述测试代码，先构建了一个`User`对象，然后进行了设置值，最后分别通过根`TemplateModel`获取不同的属性，可以看到不同的属性类型对应的是不同的`TemplateModel`子类型。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行测试，输出：

```sh
int ==> freemarker.template.SimpleNumber
String ==> freemarker.template.SimpleScalar
Phone ==> freemarker.ext.beans.StringModel
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到`int`、`String`以及自定义数据结构分别使用不同的子类型进行修饰，这么做的目的就是让模板在获取数据模型的数据时，变得有法可依，虽然用户定义了不同的数据结构，但是`FreeMarker`却可以自由的访问这些数据，完成模板与数据的合并。

### 获取数据

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果能够将用户的自定义数据结构映射到自生的数据类型上，那么就可以自由的访问用户的数据了，事实上`FreeMaker`就是这么做的。

```java
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
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过不同的`TemplateModel`子类型，可以访问`User`对象中的任意属性。有了模板`TemplateElement`抽象，将模板抽象成可以编程的状态，在通过`TemplateModel`包装用户数据，方便模板引擎对于数据的获取，这两者组合起来，就完成了`FreeMarker`最核心的工作。

> 最终的属性数据获取还是通过反射，并且Method对象会被缓存，有一定的优化，但是有一把锁，性能实际有些提升余地。虽然代码中写了使用CHM进行改造，性能预期达不到synchronized + HashMap，但是笔者认为不应该如此，再不济将缓存做到ThreadLocal中也是可以的。