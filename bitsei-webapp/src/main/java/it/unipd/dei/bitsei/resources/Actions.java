package it.unipd.dei.bitsei.resources;


/**
 * Contains constants for the actions performed by the application.
 */
public final class Actions {

    /**
     * The list of the users
     */
    public static final String LIST_USER = "LIST_USER";

    /**
     * The filter of invoices by their period of time
     */
    public static final String FILTER_INVOICES_BY_PERIOD = "FILTER_INVOICES_BY_PERIOD";

    /**
     * The filter of invoices by their total
     */
    public static final String FILTER_INVOICES_BY_TOTAL = "FILTER_INVOICES_BY_TOTAL";

    /**
     * The chart of amount of invoices ordered by date
     */
    public static final String INVOICES_BY_DATE = "INVOICES_BY_DATE";

    /**
     * This class can be neither instantiated nor sub-classed.
     */
    private Actions() {
        throw new AssertionError(String.format("No instances of %s allowed.", Actions.class.getName()));
    }
}
