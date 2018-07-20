1. 注释的格式
type in `/**`
use @param to define parameters or functions.

2. No Maven Dependancy in library
in Navigator, open .classpath.xml, 
insert ```<classpathentry kind="con" path="org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"/>  ```

#@autowired注解、@Resource注解和@Service注解
IoC container is responsible for creating and keeping the objects. The repository class (@repository) can be called by @autowired, and its methods can be accessed. IoC container seperates service and objects. 
```@autowired
private UserRepository userRepository;

public void save(){
    userRepository.save();
}
```

第二种方法
把bean的信息放在spring配置文件中
```<bean id="zoo" class="com.xrq.bean.Zoo" >
        <property name="tiger" ref="tiger" />
        <property name="monkey" ref="monkey" />
    </bean>
    
    <bean id="tiger" class="com.xrq.domain.Tiger" />
    <bean id="monkey" class="com.xrq.domain.Monkey" />
```
@Autowired最大的作用就是简略代码，消除getter和setter与bean的属性配置
接口
```public class Zoo
{
    @Autowired
    private Tiger tiger;
    
    @Autowired
    private Monkey monkey;
    
    public String toString()
    {
        return tiger + "\n" + monkey;
    }
}
```
根据spring的配置文件中的bean信息，自动注入相应的类
例子：zoo中现在可以自由调用tiger类中的数据

##@Autowired接口注入
接口有多个实现类，使用@autowired的时候spring不知道该引用哪个，所以会报错。
必须使用多个实现类时可以使用@Qualifier
```@Service
public class CarFactory
{
    @Autowired
    @Qualifier("BMW")
    private Car car;
    
    public String toString()
    {
        return car.carName();
    }
}
```
@Qualifier注解括号里写实现类的类名

##@Resource
用途和@Autowired类似
``` @Resource(name = "tiger")
    private Tiger tiger;
    
    @Resource(type = Monkey.class)
    private Monkey monkey;
```
默认通过name属性去匹配bean，找不到再用type去匹配
如果同时设置了name和type，两个必须都匹配，不然报错

和@Autowired的区别：@Resource默认用bean的名字去匹配，而@Autowired则是用类的类名

##@Service
进一步简化代码
配置文件中的bean属性可以全部删除
只要在bean类的开头添加@Service
```
@Service
public class Zoo
{
    @Autowired
    private Tiger ttiger;
    
    @Autowired
    private Monkey mmonkey;
    
    public String toString()
    {
        return ttiger + "\n" + mmonkey;
    }
}
```
Zoo在bean中的id是zoo
可以通过ApplicationContext的getBean("zoo")方法来得到Zoo.java
也可以自定义bean的id`@Service("自定义名字")`

#@ResponseBody和@RequestBody
一般情况下@Controller的方法返回的是html或jsp的页面
当返回的是其他类型的数据时，使用@ResponseBody
@ResponseBody的作用：将Controller的方法返回的对象，通过适当的HttpMessageConverter转换为指定格式后，写入到Response对象的body数据区。 
```
@RequestMapping(value = "user/login")
@ResponseBody
// 将ajax（datas）发出的请求写入 User 对象中,返回json对象响应回去
public User login(User user) {   
    User user = new User();
    user .setUserid(1);
    user .setUsername("MrF");
    user .setStatus("1");
    return user ;
}
```

@RequestBody接受的参数是json转成的string
使用post请求，同时设置contentType，`contentType:"application/json` （默认的contentType是`application/x-www-form-urlencoded; charset=UTF-8`）

#业务常规结构
##Database, DAO, DTO, Service, {Controller, Model, View}(MVC)
DAO负责从数据库获得信息，然后注入DTO中（相当于一个bean），Service中配置修改、获得数据的方法；controller中根据url的解析调用不同的方法，将数据注入modelAndView中，返还给用户

分层：数据库层、数据访问层（DAO）、业务逻辑层（Service）、表示层
分层的好处：在web应用开发中，使用Service层可以将web类的活动限制在controller中，这样可以独立的测试service层

DAO层应尽力保持简单，其功能仅仅是提供了数据库的连接，以及最简单的增删改查。除此之外，任何业务相关的操作都应该放到Service层，即Service层用来编写业务逻辑，即操作从DAO层读取的数据，或者将处理好的数据给DAO层。

看一个框架是否优秀，首先看这个框架是否是非侵入式的，其次是可扩展性，再次是解耦性和开发周期。

##Model和ModelAndView的区别
Model只是用来传输数据的，并不会进行业务的寻址。但是，ModelAndView却是可以进行业务寻址的，就是设置对应的要请求的静态文件。
Model是每一次请求都必须会带着的，但是ModelAndView是需要我们自己去新建的。

使用model的时候，方法必须返回jsp文件的名称；而modelAndView通过setViewName来设置跳转的页面，返回的是modelAndView.