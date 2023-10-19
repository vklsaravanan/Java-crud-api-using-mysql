package com.recordManagement.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;



public class Record {
	
	protected int id;
    protected int user_id;
    protected String title;
    protected String description;
    protected int category_id;
    protected boolean status;
    private Timestamp created_at;
    private Timestamp updated_at;
    
	public Record(int id) {
		super();
		this.id = id;
	}
	

	public Record(int id, int user_id, String title, String description, int category_id, boolean status,
			Timestamp created_at, Timestamp updated_at) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.description = description;
		this.category_id = category_id;
		this.status = status;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id= id ;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}


	public String getCreated_at() {
		Timestamp timestamp = this.created_at;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
	}

	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		Timestamp timestamp = this.updated_at;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
	}


	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}

	public int isStatus() {
		if (status==true) {
			return 1;
		}
		return 0;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
}
