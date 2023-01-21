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
    public static final String TABLE_ALBUMS= "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST_ID = "artist";

    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST_ID = 3;

    public static final int NO_ORDER = 0;
    public static final int ASC_ORDER = 1;
    public static final int DESC_ORDER = 2;

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
    
    public List<Artist> queryArtists(int orderOfSort) {
        StringBuilder queryString = new StringBuilder("SELECT * FROM ");
        queryString.append(TABLE_ARTISTS);

        sorting(orderOfSort, queryString);

        try (Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryString.toString())){

            List<Artist> artists = new ArrayList<>();
            while(rs.next()) {
                Artist artist = new Artist();

                artist.setId(rs.getInt(INDEX_ARTIST_ID));
                artist.setName(rs.getString(INDEX_ALBUM_NAME));

                artists.add(artist);
            }
            return artists;
        } catch (SQLException e){
            System.out.println("Failed to query..."+e.getMessage());
            return null;
        }
    }

    public List<Album> queryTheAlbumForArtist(String artistName,int orderOfSort){
        StringBuilder queryString = new StringBuilder("SELECT * FROM ");
        queryString.append(TABLE_ALBUMS + " INNER JOIN "+ TABLE_ARTISTS +
                " ON "+ TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST_ID + "=" +
                TABLE_ARTISTS + "." + COLUMN_ARTIST_ID + " WHERE " + TABLE_ARTISTS +
                "." + COLUMN_ARTIST_NAME + "=\"" + artistName + "\"");

        if(orderOfSort != NO_ORDER){
            queryString.append(" ORDER BY ");
            queryString.append(TABLE_ALBUMS+".");
            queryString.append(COLUMN_ARTIST_NAME);
            queryString.append(" COLLATE NOCASE ");

            if(orderOfSort == ASC_ORDER){
                queryString.append("ASC");
            }else {
                queryString.append("DESC");
            }
        }
        //System.out.println(queryString);

        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(queryString.toString())){

            List<Album> albums = new ArrayList<>();


            while(rs.next()){
                Album album = new Album();

                album.setId(rs.getInt(INDEX_ALBUM_ID));
                album.setName(rs.getString(INDEX_ALBUM_NAME));
                album.setArtist(rs.getInt(INDEX_ALBUM_ARTIST_ID));

                albums.add(album);
            }
            return albums;
        } catch (SQLException e){
            System.out.println("Failed to query: "+e.getMessage());
            return null;
        }
    }

    private void sorting(int orderOfSort, StringBuilder queryString) {
        if(orderOfSort != NO_ORDER){
            queryString.append(" ORDER BY ");
            queryString.append(COLUMN_ARTIST_NAME);
            queryString.append(" COLLATE NOCASE ");

            if(orderOfSort == ASC_ORDER){
                queryString.append("ASC");
            }else {
                queryString.append("DESC");
            }
        }
    }
}
