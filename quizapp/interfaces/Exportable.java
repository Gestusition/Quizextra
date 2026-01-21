package com.quizapp.interfaces;

/**
 * Interface for objects that can be exported to CSV format.
 */
public interface Exportable {

    /**
     * Converts this object to a CSV line.
     *
     * @return CSV formatted string
     */
    String toCsvLine();

    /**
     * Gets the CSV header for this type of object.
     *
     * @return CSV header string
     */
    String getCsvHeader();
}
