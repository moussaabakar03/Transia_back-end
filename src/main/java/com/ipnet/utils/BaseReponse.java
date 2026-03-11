package com.ipnet.utils;

public class BaseReponse<T> {

	private int codeRetour;
	private String description;
	private T data;
	
	public BaseReponse() {}
	
	public BaseReponse(int codeRetour, String description, T data) {
		super();
		this.codeRetour = codeRetour;
		this.description = description;
		this.data = data;
	}

	public int getCodeRetour() {
		return codeRetour;
	}

	public void setCodeRetour(int codeRetour) {
		this.codeRetour = codeRetour;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	
	
}
 
