package com.cormac.server;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.cormac.utils.ConnectionHelper;

public class AlbumDao {

	Connection conn = null;
	final int RESPONSE_OK = 200, NO_CONTENT = 204, SERVER_ERROR = 500;

	public ResultSet getAuthorization(String authentication, String message) {
		ResultSet rs = null;
		try {
			String clientId = authentication.substring(0, authentication.indexOf(";"));
			String hmacSignature = authentication.substring(authentication.indexOf(";")+1, authentication.length());
			System.out.println("DAO client id " + clientId);
			System.out.println("DAO hmacSignature " + hmacSignature);
			conn = ConnectionHelper.getConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM authenticator_table WHERE client_id = ?");
			System.out.println("After prepaafag");
			stmt.setString(1, clientId);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	public Response postAlbum(Album album) {
		try {
			System.out.println("connecting.....");
			conn = ConnectionHelper.getConnection();
			System.out.println("Connection established.....");
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO album (Album_Name, Album_Artist, Num_Tracks, Record_Label, inStock) VALUES(?,?,?,?,?)");
			stmt.setString(1, album.getName());
			stmt.setString(2, album.getArtist());
			stmt.setInt(3, album.getNumberOfTracks());
			stmt.setString(4, album.getRecordLabel());
			stmt.setBoolean(5, album.isInStock());
			stmt.executeUpdate();
			return Response.status(RESPONSE_OK).build();
		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			System.out.println("Error in storing to database at DAO Layer");
			sqle.printStackTrace();
			return Response.status(SERVER_ERROR).build();
		} finally {
			ConnectionHelper.close(conn);
		}
	}

	public List<Album> getAlbums() {
		List<Album> albums = new ArrayList<Album>();

		try {
			System.out.println("connecting.....");
			conn = ConnectionHelper.getConnection();
			System.out.println("Connection established.....");
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM album");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Album album = new Album();
				album.setName(rs.getString("Album_Name"));
				album.setArtist(rs.getString("Album_Artist"));
				album.setNumberOfTracks(rs.getInt("Num_tracks"));
				album.setRecordLabel(rs.getString("record_label"));
				album.setInStock(rs.getBoolean("inStock"));
				albums.add(album);
			}
		} catch (SQLException sqle) {
			System.out.println("Error in retrieiving from  database at DAO Layer");
			sqle.printStackTrace();
		} finally {
			ConnectionHelper.close(conn);
		}
		return albums;
	}

	public Response putAlbum(Album album) {
		try {
			System.out.println("connecting.....");
			conn = ConnectionHelper.getConnection();
			System.out.println("Connection established.....");
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE ALBUM SET ALBUM_NAME = ?, ALBUM_ARTIST = ?, NUM_TRACKS = ?, RECORD_LABEL = ?, inSTOCK = ? "
							+ "WHERE id = ?");

			stmt.setString(1, album.getName());
			stmt.setString(2, album.getArtist());
			stmt.setInt(3, album.getNumberOfTracks());
			stmt.setString(4, album.getRecordLabel());
			stmt.setBoolean(5, album.isInStock());
			stmt.setInt(6, album.getId());
			System.out.println(stmt.toString());
			stmt.executeUpdate();
			if (getById(album.getId()) != null) {
				return Response.status(RESPONSE_OK).build();
			}
		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			System.out.println("Error in storing to database at DAO Layer");
			sqle.printStackTrace();
		} finally {
			ConnectionHelper.close(conn);
		}
		return Response.status(NO_CONTENT).build();
	}

	public Response deleteAlbum(int id) {
		try {
			System.out.println("connecting.....");
			conn = ConnectionHelper.getConnection();
			System.out.println("Connection established.....");
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM album where id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("Error in deleting from database at DAO Layer");
			sqle.printStackTrace();
		}
		return Response.status(NO_CONTENT).build();
	}

	public Response getById(int id) {
		// TODO Auto-generated method stub
		System.out.println("id at dao layer: " + id);
		Album album = null;
		try {
			System.out.println("connecting.....");
			conn = ConnectionHelper.getConnection();
			System.out.println("Connection established.....");
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM album WHERE id = ?");
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				album = new Album();
				album.setId(rs.getInt("id"));
				album.setName(rs.getString("Album_Name"));
				album.setArtist(rs.getString("Album_Artist"));
				album.setNumberOfTracks(rs.getInt("Num_tracks"));
				album.setRecordLabel(rs.getString("record_label"));
				album.setInStock(rs.getBoolean("inStock"));
			}
			if (album != null) {
				return Response.ok(album).build();
			}
		} catch (SQLException sqle) {
			System.out.println("Error in retrieiving from  database at DAO Layer");
			sqle.printStackTrace();
		} finally {
			ConnectionHelper.close(conn);
		}
		return Response.status(NO_CONTENT).build();
	}

}
