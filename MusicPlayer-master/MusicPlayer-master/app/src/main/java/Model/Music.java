package Model;

public class Music {

    private String title;
    private String album;
    private String path;
    private String duration;
    private String artist;
    private String id;

    public Music(String title, String album, String path, String duration, String artist, String id) {
        this.title = title;
        this.album = album;
        this.path = path;
        this.duration = duration;
        this.artist = artist;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
