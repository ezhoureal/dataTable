package com.zhou.dao;

import java.util.List;

import com.zhou.dto.People;
public interface PeopleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(People record);

    int insertSelective(People record);

    People selectByPrimaryKey(Integer id);
    
    List<People> selectAllUser();

    int updateByPrimaryKeySelective(People record);

    int updateByPrimaryKey(People record);
}