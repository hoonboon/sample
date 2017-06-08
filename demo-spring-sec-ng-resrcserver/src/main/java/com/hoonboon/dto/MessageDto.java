package com.hoonboon.dto;

import java.util.UUID;

public class MessageDto {

	private String id = UUID.randomUUID().toString();
	private String content;
	
	public MessageDto(String content) {
		super();
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
