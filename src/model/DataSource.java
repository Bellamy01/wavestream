package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    private Connection connection;
    public static final String DB_NAME = "sound.db";
    public static final String DB_CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\BELLAMY\\IdeaProjects\\wavestream\\"+DB_NAME;

    //artists
    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";

    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    //songs
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";

    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;
    //albums
    public static final String TABLE_ALBUMS= "songs";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";

    public static final int INDEX_ALBUMS_ID = 1;
    public static final int INDEX_ALBUMS_NAME = 2;

    public boolean open(){
        try {
            connection = DriverManager.getConnection(DB_CONNECTION_STRING);
            System.out.println("Connection to the the database successful...");
            return true;
        } catch (SQLException e){
            System.out.println("Connection to database failed..."+e.getMessage());
            return false;
        }
    }

    public void close(){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e){
            System.out.println("Unable to close connection..."+e.getMessage());
        }
    }
    
    public List<Artist> queryArtists() {
        try (Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_ARTISTS)){

            List<Artist> artists = new ArrayList<>();
            while(rs.next()) {
                Artist artist = new Artist();

                int id= rs.getInt(COLUMN_ARTIST_ID);
                String name = rs.getString(COLUMN_ARTIST_NAME);

                artist.setId(id);
                artist.setName(name);

                artists.add(artist);
            }
            return artists;
        } catch (SQLException e){
            System.out.println("Failed to query..."+e.getMessage());
            return null;
        }
    }
}
