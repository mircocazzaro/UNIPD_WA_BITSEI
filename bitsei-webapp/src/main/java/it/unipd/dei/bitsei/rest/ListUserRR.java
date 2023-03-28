package it.unipd.dei.bitsei.rest;

import it.unipd.dei.bitsei.dao.ListUserDAO;
import it.unipd.dei.bitsei.resources.Actions;
import it.unipd.dei.bitsei.resources.User;
import it.unipd.dei.bitsei.resources.Message;
import it.unipd.dei.bitsei.resources.ResourceList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for listing {@link User}s.
 */
public final class ListUserRR extends AbstractRR {

    /**
     * Creates a new REST resource for listing {@code User}s.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ListUserRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.LIST_USER, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        List<User> el = null;
        Message m = null;

        try {

            // creates a new DAO for accessing the database and lists the user(s)
            el = new ListUserDAO(con).access().getOutputParam();

            if (el != null) {
                LOGGER.info("User(s) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList(el).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while listing user(s).");

                m = new Message("Cannot list user(s): unexpected error.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list user(s): unexpected database error.", ex);

            m = new Message("Cannot list user(s): unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }


}