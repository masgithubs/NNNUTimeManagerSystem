package cn.edu.tools;

import java.io.Serializable;
import java.util.Map;

public class MapDataIntent implements Serializable {
    public MapDataIntent(Map<String, String> map) {
        this.map = map;
    }

    Map<String,String> map;

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }
}
