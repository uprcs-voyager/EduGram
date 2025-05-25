package app.edugram.models;
import java.util.List;

public interface CRUDable<T> {

    boolean create(T item);

    T read(int id);

    boolean update(T item);

    boolean delete(int id);

    List<T> listAll();
}
