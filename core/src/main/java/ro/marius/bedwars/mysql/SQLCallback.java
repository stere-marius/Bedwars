package ro.marius.bedwars.mysql;

public interface SQLCallback<T> {

    void onQueryDone(T result);

}
