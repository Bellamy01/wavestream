import model.Album;
import model.Artist;
import model.ArtistSong;
import model.DataSource;

import java.util.List;
import java.util.Scanner;

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
        System.out.print("Please insert artist: ");
        Scanner scanner = new Scanner(System.in);
        String artistName = scanner.nextLine();

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
        //View song info
        if(ds.createTheViewForSongArtist()){
            System.out.println("\n\nCool, you've just created the view");
        }

        System.out.print("\n\nPlease insert song title: ");
        String songTitle = scanner.nextLine();
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