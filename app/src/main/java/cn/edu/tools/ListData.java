package cn.edu.tools;

import java.io.Serializable;
import java.util.List;

public class ListData<T extends Object> implements Serializable {
    public ListData(List<T> strings) {
        this.strings = strings;
    }

    List<T> strings;

    public List<T> getStrings() {
        return strings;
    }

    public void setStrings(List<T> strings) {
        this.strings = strings;
    }
}
