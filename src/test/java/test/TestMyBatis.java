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
 
 
//ָ��beanע��������ļ�  
@ContextConfiguration(locations = { "classpath:spring-mybatis.xml" })  
//ʹ�ñ�׼��JUnit ��ʾ�̳���SpringJUnit4ClassRunner��
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
            // logger.info("ֵ��"+user.getUserName());  
            logger.info(JSON.toJSONString(list));
            System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }   
    //����������
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
