package com.zhou.service;

import java.util.List;

import com.zhou.dto.People;

public interface PeopleService {
	public People getPeopleById(int userId);
	public void insert(People people);
	public List<People> getAllPeople();
}
