package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    private Connection connection;
    public static final String DB_NAME = "sound.db";
    public static final String DB_CONNECTION_STRING = "jdbc:sqlite:"+System.getProperty("user.dir")+"\\"+DB_NAME;

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

    public static final String TABLE_ARTISTS_SONG_VIEW = "artist_list";
    public static final String CREATE_ARTIST_FOR_SONG_VIEW =
            "CREATE VIEW IF NOT EXISTS "+TABLE_ARTISTS_SONG_VIEW+ " AS SELECT "+ TABLE_ARTISTS + "."+ COLUMN_ARTIST_NAME + ", "+ TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " AS ALBUM, "+ TABLE_SONGS + "."+ COLUMN_SONG_TRACK+","+ TABLE_SONGS + "."+ COLUMN_SONG_TITLE+  " FROM "+ TABLE_SONGS + " INNER JOIN "+
            TABLE_ALBUMS + " ON "+ TABLE_SONGS+ "."+ COLUMN_SONG_ALBUM + " = "+ TABLE_ALBUMS +"."+COLUMN_ALBUM_ID +" INNER JOIN "+
                    TABLE_ARTISTS + " ON "+ TABLE_ARTISTS +"."+ COLUMN_ARTIST_ID + " = "+ TABLE_ALBUMS+ "."+ COLUMN_ALBUM_ARTIST_ID + " ORDER BY "+
                    TABLE_ARTISTS+ "."+ COLUMN_ARTIST_NAME + ", "+ TABLE_ALBUMS + "."+ COLUMN_ALBUM_NAME + ", "+ TABLE_SONGS+ "."+ COLUMN_SONG_TRACK;

    public static final String QUERY_VIEW_SONG_TITLE_INFO = "SELECT "+ COLUMN_ARTIST_NAME + ","+ COLUMN_SONG_ALBUM + "," + COLUMN_SONG_TRACK + " FROM "+
            TABLE_ARTISTS_SONG_VIEW + " WHERE " + COLUMN_SONG_TITLE + "= \"";

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
        String queryString = complexQuery(artistName, orderOfSort, QUERY_ALBUM_FOR_THE_ARTIST_START_STRING, QUERY_THE_ALBUM_FOR_THE_ARTIST_SORT_STRING);

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
        String queryString = complexQuery(songName, orderOfSort, QUERY_ARTIST_SONG_START_STRING, QUERY_ARTIST_SONG_SORT_STRING);
        return loopSongArtistResults(queryString);
    }

    public boolean createTheViewForSongArtist(){
       try(Statement statement = connection.createStatement()){
           statement.execute(CREATE_ARTIST_FOR_SONG_VIEW);
           return true;
       } catch (SQLException e){
           System.out.println("Failed to query: "+ e.getMessage());
           return false;
       }
    }

    public List<ArtistSong> querySongInfoView(String songTitle) {
        String queryString = QUERY_VIEW_SONG_TITLE_INFO + songTitle + "\"";
        return loopSongArtistResults(queryString);
    }

    private List<ArtistSong> loopSongArtistResults(String queryString){
        try(Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(queryString)){
            List<ArtistSong> artistSongs = new ArrayList<>();

            while(rs.next()){
                ArtistSong artistSong = new ArtistSong();

                artistSong.setArtistName(rs.getString(1));
                artistSong.setAlbumName(rs.getString(2));
                artistSong.setTrack(rs.getInt(3));

                artistSongs.add(artistSong);
            }
            return artistSongs;
        } catch (SQLException e){
            System.out.println("Failed to query: "+ e.getMessage());
            return null;
        }
    }

    private String complexQuery(String songName, int orderOfSort, String queryArtistSongStartString, String queryArtistSongSortString) {
        StringBuilder queryString = new StringBuilder(queryArtistSongStartString);
        queryString.append(songName);
        queryString.append("\"");

        if(orderOfSort != NO_ORDER){
            queryString.append(queryArtistSongSortString);
            if(orderOfSort == ASC_ORDER){
                queryString.append("ASC");
            }else {
                queryString.append("DESC");
            }
        }
        System.out.println(queryString);
        return queryString.toString();
    }

    public void queryTheSongMetaData(){
        String queryString = "SELECT * FROM "+ TABLE_SONGS;
        try (Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(queryString)){
            ResultSetMetaData rsmd = rs.getMetaData();
            int numOfColumns = rsmd.getColumnCount();
            System.out.println("The number of columns for table songs: "+ numOfColumns);
            for(int i = 1; i<=numOfColumns; i++){
                System.out.println("The column "+ i+ " in table songs has the name: "+ rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.out.println("Failed to query: "+ e.getMessage());
        }
    }

    public int getCount(String tableName){
        String queryString = "SELECT COUNT(*) FROM "+ tableName;
        try (Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery(queryString)){
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Failed to query: "+ e.getMessage());
            return -1;
        }
    }
}
