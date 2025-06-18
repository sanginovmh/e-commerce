package uz.pdp.base;

import java.io.IOException;
import java.util.UUID;

public interface BaseService<T> {

    /**
     * Adds a new item of type T.
     *
     * @param t the item to add
     * @throws IOException if an I/O error occurs
     */
    void add(T t) throws IOException;

    /**
     * Retrieves an item of type T by its UUID.
     *
     * @param id the UUID of the item to retrieve
     * @return the item of type T
     */
    T get(UUID id);

    /**
     * Updates an existing item of type T.
     *
     * @param id the UUID of the item to update
     * @param t  the updated item
     * @return true if the update was successful, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean update(UUID id, T t) throws IOException;

    /**
     * Removes an item of type T by its UUID.
     *
     * @param id the UUID of the item to remove
     * @throws IOException if an I/O error occurs
     */
    void remove(UUID id) throws IOException;

    void clear() throws IOException;
}
