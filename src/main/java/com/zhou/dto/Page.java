package com.zhou.dto;

import java.io.Serializable;

public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	//��ǰҳ
    private Integer page=1;
    //ҳ��С
    private Integer rows=5;
    // �ܼ�¼ ��
    private Integer totalRecord;
    //��ҳ��
    private Integer totalPage;
    //�ؼ�������
    private String keyType;
    //��ѯ�ؼ���
    private String keyWord;
    //��ʼ��¼λ��
    private Integer start;
    //�û�id
    private String userid;
    //�����û�id
    private String otherid;

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getOtherid() {
        return otherid;
    }

    public void setOtherid(String otherid) {
        this.otherid = otherid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getTotalPage() {
        totalPage=(totalRecord-1)/rows+1;
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {

        this.totalPage = totalPage;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getStart() {
        start=(page-1)*rows;
        return start;
    }

    public void setStart(Integer start) {

        this.start = start;
    }

   
    public Page() {
    }

    public Page(Integer page, Integer rows, Integer totalRecord, Integer totalPage, String keyType, String keyWord, Integer start, String userid, String otherid) {
        this.page = page;
        this.rows = rows;
        this.totalRecord = totalRecord;
        this.totalPage = totalPage;
        this.keyType = keyType;
        this.keyWord = keyWord;
        this.start = start;
        this.userid = userid;
        this.otherid = otherid;
    }

    @Override
    public String toString() {
        return "Page{" +
                "page=" + page +
                ", rows=" + rows +
                ", totalRecord=" + totalRecord +
                ", totalPage=" + totalPage +
                ", keyType='" + keyType + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", start=" + start +
                ", userid='" + userid + '\'' +
                ", otherid='" + otherid + '\'' +
                '}';
    }
}