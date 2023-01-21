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
            System.out.println("Artists not found!");
        }
        System.out.println("===========ARTIST DETAILS=============");
        for(Artist artist : artists){
            System.out.println(artist.getId()+ " ----- "+artist.getName());
        }

        //albums
        String artistName = "Burna Boy";
        List<Album> albums = ds.queryTheAlbumForArtist(artistName,DataSource.DESC_ORDER);
        if(albums == null){
            System.out.println(artistName+ " 's albums were no found");
        }
        System.out.println("===========ALBUM DETAILS===============");
        for(Album album : albums){
            System.out.println(album.getId()+ " ----- " + album.getName());
        }
        ds.close();
    }
}