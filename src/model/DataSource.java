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

    public static final String QUERY_ARTIST_START_STRING = "SELECT * FROM "+ TABLE_ARTISTS;
    public static final String QUERY_ARTIST_SORT_STRING = " ORDER BY "+ TABLE_ARTISTS+ '.'+COLUMN_ARTIST_NAME+ " COLLATE NOCASE";

    public static final String QUERY_ALBUM_FOR_THE_ARTIST_START_STRING =
            "SELECT * FROM "+TABLE_ALBUMS + " INNER JOIN "+ TABLE_ARTISTS +
                    " ON "+ TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST_ID + "=" +
                    TABLE_ARTISTS + "." + COLUMN_ARTIST_ID + " WHERE " + TABLE_ARTISTS +
                    "." + COLUMN_ARTIST_NAME + "=\"";

    public static final String QUERY_THE_ALBUM_FOR_THE_ARTIST_SORT_STRING =
            " ORDER BY "+TABLE_ALBUMS+"."+COLUMN_ARTIST_NAME+" COLLATE NOCASE ";

    public static final String QUERY_ARTIST_SONG_START_STRING ="SELECT "+ TABLE_ARTISTS+ "."+COLUMN_ARTIST_NAME+","
            +TABLE_ALBUMS+"."+COLUMN_ALBUM_NAME+","+TABLE_SONGS+"."+COLUMN_SONG_TITLE+" FROM "+TABLE_SONGS+
            " INNER JOIN "+ TABLE_ALBUMS+ " ON "+ TABLE_SONGS+ "."+COLUMN_SONG_ALBUM + " = "+ TABLE_ALBUMS+ "."+COLUMN_ALBUM_ID
            + " INNER JOIN "+ TABLE_ARTISTS + " ON "+  TABLE_ARTISTS+ "."+COLUMN_ARTIST_ID+" = "+ TABLE_ALBUMS+"."+ COLUMN_ALBUM_ARTIST_ID+
            " WHERE " + TABLE_SONGS+"."+ COLUMN_SONG_TITLE+"=\"";

    public static final String QUERY_ARTIST_SONG_SORT_STRING =
            "ORDER BY" + TABLE_ARTISTS+ "."+ COLUMN_ARTIST_NAME+ "COLLATE NOCASE ";

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
        StringBuilder queryString = new StringBuilder(QUERY_ARTIST_START_STRING);

        if(orderOfSort != NO_ORDER){
            queryString.append(QUERY_ARTIST_SORT_STRING);
            if(orderOfSort == ASC_ORDER){
                queryString.append("ASC");
            }else {
                queryString.append("DESC");
            }
        }
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
        StringBuilder queryString = complexQuery(artistName, orderOfSort, QUERY_ALBUM_FOR_THE_ARTIST_START_STRING, QUERY_THE_ALBUM_FOR_THE_ARTIST_SORT_STRING);

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

    public List<ArtistSong> queryTheArtistSong(String songName, int orderOfSort){
        StringBuilder queryString = complexQuery(songName, orderOfSort, QUERY_ARTIST_SONG_START_STRING, QUERY_ARTIST_SONG_SORT_STRING);

        try (Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(queryString.toString())){

            List<ArtistSong> artistSongs = new ArrayList<>();

            while (rs.next()){
                ArtistSong artistSong = new ArtistSong();

                artistSong.setArtistName(rs.getString(INDEX_ARTIST_NAME));
                artistSong.setTrack(rs.getInt(INDEX_SONG_TRACK));
                artistSong.setAlbumName(rs.getString(INDEX_ALBUM_NAME));

                artistSongs.add(artistSong);
            }
            return artistSongs;
        } catch (SQLException e){
            System.out.println("Failed to query: "+ e.getMessage());
            return null;
        }
    }

    private StringBuilder complexQuery(String songName, int orderOfSort, String queryArtistSongStartString, String queryArtistSongSortString) {
        StringBuilder queryString = new StringBuilder(queryArtistSongStartString);
        queryString.append(songName+"\"");

        if(orderOfSort != NO_ORDER){
            queryString.append(queryArtistSongSortString);
            if(orderOfSort == ASC_ORDER){
                queryString.append("ASC");
            }else {
                queryString.append("DESC");
            }
        }
        System.out.println(queryString);
        return queryString;
    }
}
