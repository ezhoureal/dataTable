##首先，bean的定义
首先，bean是一个java类
Javabean在mvc模型中是model模型层；在一般的程序中，bean是数据层。
bean类必须是公共并具体的，并且具有无参数的构造器
用途：打包功能、处理、值、数据库访问
可以理解为一种随时复制黏贴的功能

#class(bean)转json
gson是目前最方便的工具了
student就是bean
1. 只有基本数据类型的数据结构
`gson.toJson(student)`
输出为{"email":"965266509@qq.com","nickName":"乔晓松","id":1,"age":22}

2. 除了基本数据类型还包含了List集合
```Gson gson = new Gson();
        Student student = new Student();
        student.id = 1;
        student.nickName = "乔晓松";
        student.age = 22;
        student.email = "965266509@qq.com";
        ArrayList<String> books = new ArrayList<String>();
        books.add("数学");
        books.add("语文");
        books.add("英语");
        books.add("物理");
        books.add("化学");
        books.add("生物");
        student.books = books;
        Log.e("MainActivity", gson.toJson(student));
```
输出为{"books":["数学","语文","英语","物理","化学","生物"],"email":"965266509@qq.com","nickName":"乔晓松","id":1,"age":22}
可以看到list加了【】

3. 同时有map和list的结构
map在json中的格式：
`{"books":["数学","语文","英语","物理","化学","生物"],"booksMap":{"3":"英语","2":"语文","1":"数学","6":"生物","5":"化学","4":"物理"},`

###map数据结构
将键映射到值的对象，一个映射不能包含重复的键，每个键映射一个值
map的新建：例`Map<String,String> map = new HashMap<String,String>();`
map的遍历
    1. 遍历键
`for(String key:map.keySet())`
    2. 遍历值
`for(String value: map.value())`
    3. 遍历键+值
```for (Map.Entry<String,String> entry: map.entrySet()){
    System.out.println(entry.getKey() + entry.getValues());
```
###注意，hashMap的遍历结果都是无序的,
HashMap里面存入的键值对在取出的时候是随机的，它根据键的HashCode值存储数据，根据键可以直接获取它的值，具有很快的访问速度。在Map中插入、删除和定位元素，HashMap 是最好的选择。 
　　TreeMap取出来的是排序后的键值对。但如果要按自然顺序或自定义顺序遍历键，那么TreeMap会更好。 
　　LinkedHashMap 如果需要输出的顺序和输入的相同，那么用LinkedHashMap可以实现，它还可以按读取顺序来排列，像连接池中可以应用。　

##用gson直接把json转换成class
`Student studentG = gson.fromJson(result, Student.class);`

泛型也可以转换
``` public HashMap<String,Book> booksMap;
 
 public class Book{
        public int id;
        public String name;
    }
```
因为有自定义的类型，不能直接转换，要使用typetoken
`HashMap<String, Book> booksMap = gson.fromJson(result, new TypeToken<HashMap<String, Book>>() { }.getType());`

#用gson解析复杂数据
```{
  "code": 200,
  "msg": "OK",
  "muser": [
    {
      "name": "zhangsan",
      "age": "10",
      "phone": "11111",
      "email": "11111@11.com"
    },
    {
      "name": "lisi",
      "age": "20",
      "phone": "22222",
      "email": "22222@22.com"
    },
    ...
  ]
}
```
使用class的嵌套，如下
```public class ResultBean {
    //注意变量名与字段名一致
    private int code;
    private String msg;
    private List<UserBean> muser;

    public class UserBean{
        private String name ;
        private String age;
        private String phone;
        private String email;
    }
    ...
}
```
先用gson解析成对象
`ResultBean resultBean = new Gson().fromJson(strByJson,ResultBean.class);`
再从中提取userbean的list
`List<ResultBean.UserBean> userBeanList = resultBean.getMuser();`

##当不需要所有json数组时
```{
  "code": 200,
  "msg": "OK",
  "muser": [
    {
      "name": "zhangsan",
      "age": "10",
      "phone": "11111",
      "email": "11111@11.com"
    },
    {
      "name": "lisi",
      "age": "20",
      "phone": "22222",
      "email": "22222@22.com"
    },
    ...
  ]
}
```
先使用jsonObject和JsonArray获取内部数组
```//最外层
JsonObject jsonObject = new JsonParser().parse(strByJson).getAsJsonObject();
//需要遍历的数组
JsonArray jsonArray = jsonObject.getAsJsonArray("muser");
```
用jsonElement进行遍历，将每组数据存入bean,所有bean存入beanList
```List<UserBean> userBeanList = new ArrayList<>();
for (JsonElement user : jsonArray) {
    UserBean userBean = new Gson().fromJson(user, new TypeToken<UserBean>() {}.getType());
    userBeanList.add(userBean);
}
```
所有需要的数据都存到beanList中了