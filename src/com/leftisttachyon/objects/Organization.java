package com.leftisttachyon.objects;

import java.io.Serializable;

public class Organization implements Serializable {
	private static final long serialVersionUID = 1052L;
	
	public final String description, name, imageURL;
	
	public Organization(String imageURL, String description, String name) {
		this.imageURL = imageURL;
		this.description = description;
		this.name = name;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		try {
			return "<div class=\"center\" style=\"background: white; padding: 13px;\"><h3>" + name + "</h3><br><img src=\"" + imageURL + "\" class=\"center\"></img><br><br><p>" + description + "</p></div>";
		} catch(Exception e) {
			return null;
		}
	}
}
