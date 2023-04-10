package it.unipd.dei.bitsei.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.bitsei.dao.ListBankAccountsDAO;
import it.unipd.dei.bitsei.resources.Actions;
import it.unipd.dei.bitsei.resources.BankAccount;
import it.unipd.dei.bitsei.resources.Company;
import it.unipd.dei.bitsei.resources.Message;
import it.unipd.dei.bitsei.resources.ResourceList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Handiling the listing of all bank accounts givin a a company
 *
 * @author Nicola Boscolo
 * @version 1.00
 * @since 1.00
 */
public class ListBankAccountsRR extends AbstractRR{

    /**
     * lists all bank accounts
     *
     * @param action the action to log
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ListBankAccountsRR(String action, HttpServletRequest req, HttpServletResponse res, Connection con) {
        super(Actions.LIST_BANK_ACCOUNTS, req, res, con);
    }

    /**
     * lists all bank accounts
     */
    @Override
    protected void doServe() throws IOException{
        List<BankAccount> el = null;
        Message m = null;

        

        try {
            Company c = Company.fromMultiPart(req);

            // creates a new DAO for accessing the database and lists the user(s)
            el = new ListBankAccountsDAO(con, c).access().getOutputParam();

            if (el != null) {
                LOGGER.info("bank account(s) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList(el).toJSON(res.getOutputStream());

            } else { // it should not happen
                LOGGER.error("Fatal error while listing bank account(s).");

                m = new Message("Cannot list bank account(s): unexpected error.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list bank account(s): unexpected database error.", ex);

            m = new Message("Cannot list bank account(s): unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }catch (ServletException ex){
            LOGGER.error("Cannot list bank account(s): unexpected servletExpection error error.", ex);

            m = new Message("Cannot list bank account(s): unexpected servletExpection error error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
    
}
