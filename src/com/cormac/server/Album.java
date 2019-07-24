package com.cormac.server;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Album {
	private int id;
	private String name;
	private String artist;
	private int numberOfTracks;
	private String recordLabel;
	private boolean inStock;
	
	public int getId() {
		return id; 
	}

	public void setId(int id) {
		this.id = id; 
	}
 
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public int getNumberOfTracks() {
		return numberOfTracks;
	}
	
	public void setNumberOfTracks(int numberOfTracks) {
		this.numberOfTracks = numberOfTracks;
	}
	
	public String getRecordLabel() {
		return recordLabel;
	}
	
	public void setRecordLabel(String recordLabel) {
		this.recordLabel = recordLabel;
	}
	
	public boolean isInStock() {
		return inStock;
	}
	
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", name=" + name + ", artist=" + artist + ", numberOfTracks=" + numberOfTracks
				+ ", recordLabel=" + recordLabel + ", inStock=" + inStock + "]";
	}
	
	
	
}
