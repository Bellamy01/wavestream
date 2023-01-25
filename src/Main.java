import model.Album;
import model.Artist;
import model.ArtistSong;
import model.DataSource;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataSource ds = new DataSource();
        ds.open();
        if(!ds.open()){
            System.out.println("Couldn't open the database...");
        }
        //artists
        List<Artist> artists = ds.queryArtists(DataSource.NO_ORDER);
        if(artists == null){
            System.out.println("Artists were not found!");
        }else {
            if(artists.size() == 0){
                System.out.println("No artist found");
            }else {
                System.out.println("===========ARTIST DETAILS=============");
                for (Artist artist : artists) {
                    System.out.println(artist.getId() + " ----- " + artist.getName());
                }
            }
        }
        //albums
        String artistName = "Maroon 5";
        List<Album> albums = ds.queryTheAlbumForArtist(artistName,DataSource.DESC_ORDER);
        if(albums == null){
            System.out.println("No albums, query failed");
        }else {
            if(albums.size() == 0){
                System.out.println("No "+artistName+"'s albums were found");
            }else {
                System.out.println("\n===========ALBUM DETAILS===============");
                for (Album album : albums) {
                    System.out.println(album.getName());
                }
            }
        }
        //songs
        String songName = "24K Magic";
        List<ArtistSong> artistSongs = ds.queryTheArtistSong(songName,DataSource.NO_ORDER);
        if(artistSongs == null){
            System.out.println("No artist for specified song, query failed");
        }else {
            if(artistSongs.size() == 0){
                System.out.println("No "+songName+"'s songs were found");
            }else {
                System.out.println("\n===========SONG DETAILS===============");
                for (ArtistSong artistSong : artistSongs) {
                    System.out.println(artistSong.getArtistName()+ " ----- "+ artistSong.getAlbumName()+ " ---- "+ artistSong.getTrack());
                }
            }
        }

        //View song info
        if(ds.createTheViewForSongArtist()){
            System.out.println("Cool, you've just created the view");
        }

        String songTitle = "7 rings";
        List<ArtistSong> artistSongs1 = ds.querySongInfoView(songTitle);
        if(artistSongs1.isEmpty()){
            System.out.println("Empty, could not find results for that title!!");
            return;
        }
        System.out.println("\n===========SONG INFO FROM VIEW===============");
            for (ArtistSong artistSong : artistSongs1) {
                System.out.println(artistSong.getArtistName()+ " ----- "+ artistSong.getAlbumName()+ " ---- "+ artistSong.getTrack());
            }
        //Result set meta-data
        System.out.println("\n\n=============META DATA===============");
        ds.queryTheSongMetaData();
        int count = ds.getCount(DataSource.TABLE_SONGS);
        System.out.println("\nYou have "+ count + " records in songs");

        ds.close();
    }
}