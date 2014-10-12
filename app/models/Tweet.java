package models;

public class Tweet {
	public String coordinates;
	public String geo;
	public String place;
	public String timestamp;
	
	public Tweet(String coordinates, String geo, String place, String timestamp)
	{
		this.coordinates = coordinates;
		this.geo = geo;
		this.place = place;
		this.timestamp = timestamp;
	}
}
