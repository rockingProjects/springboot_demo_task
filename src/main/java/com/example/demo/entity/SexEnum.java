package com.example.demo.entity;

public enum SexEnum implements EnumI {
	male("male"),
	female("female");
	
	private String text;
	
	SexEnum(String text) {
		this.text = text;
	}
	
	@Override
	public String getText() {
		return this.text;
	}
}
