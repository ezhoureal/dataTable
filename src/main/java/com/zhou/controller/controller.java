package com.zhou.controller;
import java.util.List;

import javax.annotation.Resource;  
import org.springframework.stereotype.Controller;  
import org.springframework.ui.Model;  
import org.springframework.web.bind.annotation.RequestMapping;
import com.zhou.dto.People;
import com.zhou.service.PeopleService;  
  
@Controller  
public class controller {  
    @Resource  
    private PeopleService peopleService;  
      /*
    @RequestMapping("/showUser")  
    public String toIndex(HttpServletRequest request,Model model){  
        int userId = Integer.parseInt(request.getParameter("id"));  
        People people = this.peopleService.getPeopleById(userId);  
        model.addAttribute("people",people);  
        return "showUser";  
    }																							
    */
    @RequestMapping("/table.do")
    public String getList(Model model){
    	List<People> lists = peopleService.getAllPeople();
    	model.addAttribute("people",lists);
    	return "table";
    }
}