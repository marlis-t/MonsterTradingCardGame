package app.daos;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Dao<T> {
    T create(T t) throws SQLException;
    T read(String id) throws SQLException;
    ArrayList<T> readAll() throws SQLException;

    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
}
