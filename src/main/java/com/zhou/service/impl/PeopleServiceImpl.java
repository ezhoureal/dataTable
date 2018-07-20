package com.zhou.service.impl;

import com.zhou.dto.People;
import com.zhou.dao.PeopleMapper;
import com.zhou.service.PeopleService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
@Service ("PeopleService")
public class PeopleServiceImpl implements PeopleService{
	@Resource
	private PeopleMapper peopleMapper;
	public People getPeopleById(int userId) {
		return this.peopleMapper.selectByPrimaryKey(userId);
	}
	public void insert(People people) {
		this.peopleMapper.insert(people);
	}
	public List<People> getAllPeople(){
		List<People> lists = new ArrayList<People>();
		lists = peopleMapper.selectAllUser();
		return lists;
	}
}
