package com.recordManagement.web;

import java.util.List;

import com.recordManagement.model.Record;

public class ResponseData{
	private String request_id = null;
	private Record data = null;
	List<Record> datas = null;
	String message = null;

	public void setMessage(String message) {
		this.message = message;
	}
	//for individual record
	public ResponseData(String request_id, Record data, String message) {
		super();
		this.request_id = request_id;
		this.data = data;
		this.message = message;
	}
	//for bulk record
	public ResponseData(String request_id, List<Record> datas, String message) {
		super();
		this.request_id = request_id;
		this.datas = datas;
		this.message = message;
	}
	// for failure reuquests
	public ResponseData(String request_id, String message) {
		super();
		this.request_id = request_id;
		this.message = message;
	}

	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public Record getData() {
		return data;
	}
	public void setData(Record data) {
		this.data = data;
	}
}
