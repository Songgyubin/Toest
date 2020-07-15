package hanium.toast.mytoast.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LCDataBase extends RealmObject {


    @PrimaryKey
    private String music_id;

    private String music_name;
    private String music_duration;

    public LCDataBase(){}

    public String getMusic_duration() {
        return music_duration;
    }

    public void setMusic_duration(String music_duration) {
        this.music_duration = music_duration;
    }

    public LCDataBase(String music_id, String music_name, String music_duration) {
        this.music_id = music_id;
        this.music_name = music_name;
        this.music_duration = music_duration;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getMusic_name() {
        return music_name;
    }

    public String getMusic_id() {
        return music_id;
    }
}
