package app.DataStructures;

import java.util.Objects;

public class JsonEntry {

    private String title;
    private String url;

    public JsonEntry (String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonEntry that = (JsonEntry) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, title);
    }

    public String getUrl () {
        return url;
    }

    public String getTitle () {
        return title;
    }

    public void setUrl (String url) {
        this.url = url;
    }
}
