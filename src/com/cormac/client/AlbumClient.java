package com.cormac.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

public class AlbumClient {

	private final static int POST = 1, GET = 2, PUT = 3, DELETE = 4, GET_BY_ID = 5, QUIT = 0;
	private final static String BASE_URI = "http://localhost:8080/DistributedSystemsAssignment";
	private final static String CLIENT_ID = "client 001";
	private final static String secretKey = "OSix5UrA8aQtcREe5f6PUpzn02Yk06dOOymplOdl4JI=";
	private final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * creates and returns the base uri
	 * 
	 * @return a reference to the base uri
	 */
	private static URI getBaseURI() {
		return UriBuilder.fromUri(BASE_URI).build();
	}

	public static void main(String[] args) {
		menu();
	}

	/**
	 * Menu for guiding user through application
	 */
	private static void menu() {
		int option = -1;

		while (option != QUIT) {
			System.out.println("Menu");
			System.out.println("--------------------");
			System.out.println("Choose an option: ");
			System.out.println("press '1' to post");
			System.out.println("press '2' to get");
			System.out.println("press '3' to update");
			System.out.println("press '4' to delete");
			System.out.println("press '5' to get a particular album");
			System.out.println("Press '0' to exit");
			try {
				option = Integer.valueOf(br.readLine());
			} catch (NumberFormatException nfe) {
				System.out.println("Error in input - try again");
				continue;
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				System.out.println("Error in input - try again");
				continue;
			}

			switch (option) {
			case POST:
				post();
				break;
			case GET:
				get();
				break;
			case PUT:
				put();
				break;
			case DELETE:
				delete();
				break;
			case GET_BY_ID:
				getById();
				break;
			case QUIT:
				System.out.println("=============");
				System.out.println("Exiting");
				option = QUIT;
				break;
			default:
				System.out.println("Invalid option, please try again");
				break;
			}
		}
	}

	private static String getMacSignature(String message) {
		String encodedHmac = null;
		
		Mac mac = Mac.getInstance("HmacSHA256");
		byte[] messageArray = message.getBytes();
		byte[] decodedKey = Base64.getDecoder().decode(secretKey);
		
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		mac.init(originalKey);
		
		byte[] hmac = mac.doFinal(messageArray);
		encodedHmac = Base64.getEncoder().encodeToString(hmac);

		return encodedHmac;
		// End this function and my life.
	}

	/**
	 * Prompts the user to enter details for a put/post request
	 * 
	 * @return Form: The details entered by the user
	 */
	private static Form getDetails() {
		Form form = new Form();
		boolean successful = false;
		while (!successful) {
			try {
				System.out.print("Enter album name:\t");
				form.add("name", br.readLine());
				System.out.println("\nEnter album artist:\t");
				form.add("artist", br.readLine());
				System.out.println("\nEnter Number of Tracks:\t");
				form.add("numberOfTracks", Integer.valueOf(br.readLine()));
				System.out.println("\nEnter record label:\t");
				form.add("recordLabel", br.readLine());
				System.out.println("\nIs this album in stock (true/false):\t");
				try {
					form.add("inStock", Boolean.valueOf(br.readLine()));
				} catch (Exception e) {
					System.out.println("Error in input");
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Error in input - try again");
				continue;
			} catch (IOException ioe) {
				System.out.println("Error in input - try again");
				continue;
			}
			successful = true;
		}
		return form;
	}

	/**
	 * Prompts user to enter an id for a get by id request
	 * 
	 * @return id The id entered by the user
	 */
	private static String getUniqueId() {
		System.out.println("Enter an id");
		String id = null;
		try {
			id = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Creates and performs a post request
	 */
	private static void post() {
		Form form = getDetails();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String mac = getMacSignature(BASE_URI + "/rest/resources");
		System.out.println(service.path("rest").path("resources").header("authentication", CLIENT_ID + ";" + mac)
				.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, form));
	}

	/**
	 * Creates and performs a get request
	 */
	private static void get() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String mac = getMacSignature("DistributedSystemsAssignment");
		// GET
		try {
			System.out.println(service.path("rest").path("resources").header("authentication", mac)
					.accept(MediaType.TEXT_XML).get(String.class));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String getId() {
		System.out.println("Enter id: ");
		String id = null;
		try {
			id = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Creates and performs a put request
	 */
	private static void put() {
		Form form = getDetails();
		String id = getId();
		form.add("id", id);

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String mac = getMacSignature("DistributedSystemsAssignment");
		System.out.println(service.path("rest").path("resources/" + id).header("authentication", mac)
				.type(MediaType.APPLICATION_FORM_URLENCODED).put(ClientResponse.class, form));
	}

	/**
	 * Creates and performs a delete request
	 */
	private static void delete() {
		String album = getUniqueId();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String mac = getMacSignature(BASE_URI);
		System.out.println(service.path("rest").path("resources/" + album).header("authentication", mac)
				.delete(ClientResponse.class));
	}

	/**
	 * creates and performs a get by id request
	 */
	private static void getById() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String mac = getMacSignature("DistributedSystemsAssignment");
		String id = getId();

		// GET
		try {
			System.out.println(service.path("rest").path("resources/" + id).header("authentication", mac)
					.accept(MediaType.TEXT_XML).get(String.class));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
