package com.quizapp.interfaces;

/**
 * Interface for searchable objects.
 */
public interface Searchable {

    /**
     * Searches this object for a matching query.
     *
     * @param query the search query
     * @return true if the query matches
     */
    boolean search(String query);
}
