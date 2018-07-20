package test;  
  
import java.util.List;

import javax.annotation.Resource;
 
 
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
 
import com.alibaba.fastjson.JSON;
import com.zhou.dto.People;
import com.zhou.service.PeopleService;  
 
 
//指定bean注入的配置文件  
@ContextConfiguration(locations = { "classpath:spring-mybatis.xml" })  
//使用标准的JUnit 表示继承了SpringJUnit4ClassRunner类
@RunWith(SpringJUnit4ClassRunner.class) 
public class TestMyBatis{  
    private static Logger logger = Logger.getLogger(TestMyBatis.class);  
    @Resource
    private PeopleService peopleService;  
   
    @Test
    public void test1() {
    	try {
    		List<People> list = peopleService.getAllPeople();  
            //System.out.println(user.getUserName());  
            // logger.info("值："+user.getUserName());  
            logger.info(JSON.toJSONString(list));
            System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }   
    //事务管理测试
    @Test  
    public void addUserTest(){
    	try {
    		People people = new People();
            people.setUserName("kakljv");
            people.setPassword("876321");
            people.setAge(52);
            peopleService.insert(people);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
