package hanium.toast.mytoast.data;

import java.util.HashMap;

public class MyLCListData {

    private String path;
    private String id;

    private HashMap<String, String> title_id = new HashMap<String, String>();

    public MyLCListData(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
