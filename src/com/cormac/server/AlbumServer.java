package com.cormac.server;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import com.sun.jndi.toolkit.url.Uri;

@Path("/resources")
@XmlRootElement(name = "Album")
public class AlbumServer {
	
	AlbumDao albumDao = new AlbumDao();
	private static boolean isAuthorized = false;
	private final static int NOT_AUTHORIZED = 401;
	
	
	private boolean checkAuthorization(String authentication, String message){
		String clientId = authentication.substring(0, authentication.indexOf(";"));
		String hmacSignature = authentication.substring(authentication.indexOf(";")+1, authentication.length());
		try {
			ResultSet rs = albumDao.getAuthorization(authentication, message); 
			rs.next();
			String storedClientId = rs.getString("client_id");
			String secretKey = rs.getString("Encoded_key");
			System.out.println("The secret key on server side: " + secretKey);
			byte[] decodedKey = Base64.getDecoder().decode(secretKey);
			SecretKey sk = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
			Mac mac;
			mac = Mac.getInstance("HmacSHA256");
			mac.init(sk);
			byte [] messageArray = message.getBytes();
			byte[] hmac = mac.doFinal(messageArray);
			String encodedMac = Base64.getEncoder().encodeToString(hmac);
			System.out.println("Servers hmac signature is: " + encodedMac);
			if (clientId.equals(storedClientId) && hmacSignature.equals(encodedMac) && hmacSignature.length() == encodedMac.length()) {
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if the user is authorized and performs a post
	 * @param name The name of the album
	 * @param artist The albums artist
	 * @param numberOfTracks The number of tracks on the album
	 * @param recordLabel The albums record label
	 * @param inStock Boolean to deterimine if albums is currently in stock
	 * @param authentication The password for performing a post reqest
	 * @return Response code to determine if post was successful
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postAlbum(@Context UriInfo uriInfo,
			@FormParam("name") String name,
			@FormParam("artist") String artist, 
			@FormParam("numberOfTracks") int numberOfTracks,
			@FormParam("recordLabel") String recordLabel,
			@FormParam("inStock") boolean inStock,
			@HeaderParam("authentication") String authentication) {
		 System.out.println("Server: " + authentication);
		URI uri = uriInfo.getAbsolutePath();
		String message = uri.toString();
	     System.out.println(message);
		isAuthorized = checkAuthorization(authentication, message);
		
		if(isAuthorized){
			System.out.println("Number of Tracks at ws layer: " + numberOfTracks);
			Album album = new Album();
			album.setName(name);
			album.setArtist(artist);
			album.setNumberOfTracks(numberOfTracks);
			album.setRecordLabel(recordLabel);
			album.setInStock(inStock);
			isAuthorized = false;
			return albumDao.postAlbum(album);
		}else{
			return Response.status(NOT_AUTHORIZED).build();
		}
	}
	
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/{id}")
	public Response putAlbum(@FormParam("id") int id,
			@FormParam("name") String name,
			@FormParam("artist") String artist, 
			@FormParam("numberOfracks") int numberOfTracks,
			@FormParam("recordLabel") String recordLabel,
			@FormParam("inStock") boolean inStock,
			@HeaderParam("authentication") String authentication,
			@Context HttpServletResponse servletResponse) {
		
//		isAuthorized = albumDao.getAuthorization(authentication);
		
		if(isAuthorized){
			Album album = new Album();
			album.setId(id); 
			album.setName(name);
			album.setArtist(artist);
			album.setNumberOfTracks(numberOfTracks);
			album.setRecordLabel(recordLabel);
			album.setInStock(inStock);
			return albumDao.putAlbum(album);
		}else{
			return Response.status(NOT_AUTHORIZED).build();
		}
	}
	
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response getAlbums(@HeaderParam("authentication") String authentication){
		if(isAuthorized){
			List<Album> albums = albumDao.getAlbums();
			GenericEntity<List<Album>> list = new GenericEntity<List<Album>>(albums) {
	        };
	        return Response.ok(list).build();
		}else{
			return Response.status(NOT_AUTHORIZED).build();
		}
	}
	
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/{id}")
	public Response getById(@PathParam("id") int id, @HeaderParam("authentication") String authentication){
//		isAuthorized = albumDao.getAuthorization(authentication);
		if(isAuthorized){
			return albumDao.getById(id);
		}else{
			return Response.status(NOT_AUTHORIZED).build();
		}
	}
	
	@DELETE
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.TEXT_XML)
	@Path("/{id}")
	public Response deleteAlbum(@PathParam("id") int id, @HeaderParam("authentication") String authentication){
//		isAuthorized = albumDao.getAuthorization(authentication);
		if(isAuthorized){
			return albumDao.deleteAlbum(id);
		}else{
			return Response.status(NOT_AUTHORIZED).build();
		}
	}
}
 