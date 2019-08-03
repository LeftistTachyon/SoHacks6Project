package com.leftisttachyon.objects;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

public class Organization implements Serializable {
	private static final long serialVersionUID = 1052L;
	
	public final BufferedImage organizationImage;
	
	public final String description, name;
	
	public Organization(BufferedImage organizationImage, String description, String name) {
		this.organizationImage = organizationImage;
		this.description = description;
		this.name = name;
	}
	
	public BufferedImage getImage() {
		return organizationImage;
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
			File file = File.createTempFile("img", ".png");
			ImageIO.write(organizationImage, "png", file);
			byte[] fileContent = FileUtils.readFileToByteArray(file);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			return "<div class=\"center\"><h3>" + name + "</h3><br><img src=\"" + encodedString + "\" class=\"center\"></img><br><br><p>" + description + "</p></div>";
		} catch(Exception e) {
			return null;
		}
	}
}
