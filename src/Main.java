import model.Album;
import model.Artist;
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
        ds.close();
    }
}