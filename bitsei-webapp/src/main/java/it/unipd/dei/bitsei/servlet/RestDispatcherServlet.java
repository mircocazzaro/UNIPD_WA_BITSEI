package it.unipd.dei.bitsei.servlet;

import it.unipd.dei.bitsei.resources.User;
import it.unipd.dei.bitsei.resources.BankAccount;
import it.unipd.dei.bitsei.resources.LogContext;
import it.unipd.dei.bitsei.resources.Message;
import it.unipd.dei.bitsei.rest.*;
import it.unipd.dei.bitsei.rest.customer.CreateCustomerRR;
import it.unipd.dei.bitsei.rest.customer.DeleteCustomerRR;
import it.unipd.dei.bitsei.rest.customer.GetCustomerRR;
import it.unipd.dei.bitsei.rest.customer.UpdateCustomerRR;
import it.unipd.dei.bitsei.utils.RestURIParser;
import it.unipd.dei.bitsei.rest.bankAccount.CreateBankAccountRR;
import it.unipd.dei.bitsei.rest.bankAccount.DeleteBankAccountRR;
import it.unipd.dei.bitsei.rest.bankAccount.GetBankAccountRR;
import it.unipd.dei.bitsei.rest.bankAccount.ListBankAccountsRR;
import it.unipd.dei.bitsei.rest.bankAccount.UpdateBankAccoutRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import it.unipd.dei.bitsei.resources.User;
import it.unipd.dei.bitsei.resources.Invoice;
import it.unipd.dei.bitsei.resources.Customer;
import it.unipd.dei.bitsei.resources.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Dispatches the request to the proper REST resource.
 */
public final class RestDispatcherServlet extends AbstractDatabaseServlet {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException {

        LogContext.setIPAddress(req.getRemoteAddr());

        final OutputStream out = res.getOutputStream();

        try {
            // if the requested resource was a User, delegate its processing and return
            if (processUser(req, res)) {
                return;
            }
            if (processLogin(req, res)) {
                return;
            }
            if (processCompany(req, res)) {
                return;
            }
            if(processBank(req, res)){
                return;
            }

            // if the requested resource was an User, delegate its processing and return
            if (processCustomer(req, res)) {
                return;
            }

            // if the requested resource was a list of invoices, delegate its processing and return
            if (processListInvoice(req, res)) {
                return;
            }

            // if the requested resource was a list of customers, delegate its processing and return
            if (processListCustomer(req, res)) {
                return;
            }

            // if the requested resource was a list of products, delegate its processing and return
            if (processListProduct(req, res)) {
                return;
            }

            // if the requested resource was a list of filtered invoices, delegate its processing and return
            if(processListInvoiceByFilters(req, res)) {
                return;
            }

            // if none of the above process methods succeeds, it means an unknown resource has been requested
            LOGGER.warn("Unknown resource requested: %s.", req.getRequestURI());

            final Message m = new Message("Unknown resource requested.", "E4A6",
                    String.format("Requested resource is %s.", req.getRequestURI()));
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(out);
        } catch (Throwable t) {
            LOGGER.error("Unexpected error while processing the REST resource.", t);

            final Message m = new Message("Unexpected error.", "E5A1", t.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(out);
        } finally {

            // ensure to always flush and close the output stream
            if (out != null) {
                out.flush();
                out.close();
            }

            LogContext.removeIPAddress();
        }
    }

    /**
     * Checks whether the request if for an {@link User} resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for an {@code User}; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processBank(HttpServletRequest req, HttpServletResponse res) throws Exception{
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;
        RestURIParser uri;

        LOGGER.info(path);
        try{
            uri = new RestURIParser(path);

        }catch(Exception e){
            return false;
        }

        if(!uri.getResource().equals("bankaccount")){
            return false;
        }

        if(uri.getResourceID()==-1){
            /*
            * GET: gets all the bank accounts of a given company
            * POST: creates a BANK account
            */
            switch (method) {
                case "GET":
                    new ListBankAccountsRR(req, res, getConnection()).serve();
                break;
                case "POST":
                    new CreateBankAccountRR(req, res, getConnection()).serve();
                break;
                default:
                    LOGGER.warn("Unsupported operation for URI /bankaccounts: {}.", method);

                    m = new Message("Unsupported operation for URI /bankaccounts.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    res.setContentType("text/plain");
                    m.toJSON(res.getOutputStream());
                break;
            }
        }else{
            switch (method) {
                /*
                * GET: gives a bank account
                * PUT: updates a bank account
                * DELETE: deletes bank account
                */
                case "GET":
                    new GetBankAccountRR(req, res, getConnection()).serve();
                break;
                case "PUT":
                    new UpdateBankAccoutRR(req, res, getConnection()).serve();
                break;
                case "DELETE":
                    new DeleteBankAccountRR(req, res, getConnection()).serve();
                break;
                default:
                    LOGGER.warn("Unsupported operation for URI /bankaccount: {}.", method);

                    m = new Message("Unsupported operation for URI /bankaccount.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    res.setContentType("text/plain");
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;
    }


    /**
     * Checks whether the request if for an {@link User} resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for an {@code User}; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processUser(final HttpServletRequest req, final HttpServletResponse res) throws Exception {

        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a user
        if (path.lastIndexOf("rest/user") <= 0) {
            return false;
        }

        // strip everything until after the /user
        path = path.substring(path.lastIndexOf("user") + 4);

        // the request URI is: /user
        // if method GET, list users
        // if method POST, create user
        if (path.length() == 0 || path.equals("/")) {

            switch (method) {
                case "GET":
                    new ListUserRR(req, res, getConnection()).serve();
                    break;
                case "POST":
//                    new CreateUserRR(req, res, getConnection()).serve();
                    break;
                default:
                    LOGGER.warn("Unsupported operation for URI /user: {}.", method);

                    m = new Message("Unsupported operation for URI /user.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        } else if (path.equals("/reset-password")) {
            if (method.equals("POST")) {
                new RestPasswordRR(req, res, getConnection()).serve();
            }
        } else if (path.equals("/change-password")) {
            if (method.equals("POST")) {
                new ChangePasswordRR(req, res, getConnection()).serve();
            }
        }


        return true;

    }

    private boolean processLogin(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException {
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a login
        if (path.lastIndexOf("rest/login") <= 0) {
            return false;
        }

        // strip everything until after the /login
        path = path.substring(path.lastIndexOf("login") + 5);

        // the request URI is: /login
        // if method POST, authenticate the user
        if (path.length() == 0 || path.equals("/")) {
            if (method.equals("POST")) {
                new LoginUserRR(req, res, getConnection()).serve();
            } else {
                LOGGER.warn("Unsupported operation for URI /user: {}.", method);

                m = new Message("Unsupported operation for URI /user.", "E4A5",
                        String.format("Requested operation %s.", method));
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                m.toJSON(res.getOutputStream());
            }
        }

        return true;

    }


    private boolean processCompany(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a company
        if (path.lastIndexOf("rest/company") <= 0) {
            return false;
        }

        // strip everything until after the /company
        path = path.substring(path.lastIndexOf("company") + 7);

        // the request URI is: /company
        // if method GET, list companies
        // if method POST, create company
        if (path.length() == 0 || path.equals("/")) {
            switch (method) {
                case "GET" -> new ListCompanyRR(req, res, getConnection()).serve();
                case "POST" -> new CreateCompanyRR(req, res, getConnection()).serve();
                default -> {
                    LOGGER.warn("Unsupported operation for URI /user: {}.", method);
                    m = new Message("Unsupported operation for URI /company.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                }
            }
        } else if (path.matches("/image/\\d+")) {
            // the request URI is: /company/image/{id}
            // if method GET, get company image
            if (method.equals("GET")) {
                new GetCompanyImageRR(req, res, getConnection()).serve();
            }
        } else if (path.matches("/\\d+")) {
            // the request URI is: /company/{id}
            // if method GET, get company
            // if method PUT, update company
            // if method DELETE, delete company
            switch (method) {
                case "GET" -> new GetCompanyRR(req, res, getConnection()).serve();
                case "PUT" -> new UpdateCompanyRR(req, res, getConnection()).serve();
                // TODO: implement delete company -> as there is a lot of constraints we should delete them first in the right order
                // after that we can delete the company itself. -> use atomic transactions
                case "DELETE" -> new DeleteCompanyRR(req, res, getConnection()).serve();
                default -> {
                    LOGGER.warn("Unsupported operation for URI /company: {}.", method);
                    m = new Message("Unsupported operation for URI /company.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                }
            }
        }
        return true;

    }


    /**
     * Checks whether the request if for an {@link User} resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for an {@code User}; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processCustomer(final HttpServletRequest req, final HttpServletResponse res) throws Exception {

        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not an user
        if (path.lastIndexOf("rest/customer") <= 0) {
            return false;
        }

        // strip everything until after the /user
        path = path.substring(path.lastIndexOf("customer") + 8);

        // the request URI is: /user
        // if method GET, list users
        // if method POST, create user
        if (path.length() == 0 || path.equals("/")) {

            switch (method) {
                /*case "GET":
                    new ListCustomerRR(req, res, getConnection()).serve();
                    break;*/
                case "POST":
                    new CreateCustomerRR(req, res, getConnection()).serve();
                    break;
                 /*case "DELETE":
                    new DeleteCustomerRR(req, res, getConnection()).serve();
                    break;*/
                default:
                    LOGGER.warn("Unsupported operation for URI /customer: %s.", method);

                    m = new Message("Unsupported operation for URI /customer.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        else if (path.matches("/\\d+")) {
            switch (method) {
                case "GET":
                    new GetCustomerRR(req, res, getConnection()).serve();
                    break;
                case "DELETE":
                    new DeleteCustomerRR(req, res, getConnection()).serve();
                    break;
                case "PUT":
                    new UpdateCustomerRR(req, res, getConnection()).serve();
                    break;


                default:
                    LOGGER.warn("Unsupported operation for URI /customer: %s.", method);

                    m = new Message("Unsupported operation for URI /customer.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;

    }

    /**
     * Checks whether the request is for a list of {@link Invoice}s resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for a list of {@code Invoice}s; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processListInvoice(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final int companyId = -1; //TODO: replace with currentUser_CompanyId, ask to autent. subgroup
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a list-invoice
        if (path.lastIndexOf("rest/list-invoice") <= 0) {
            return false;
        }

        // strip everything until after the /list-invoice
        path = path.substring(path.lastIndexOf("list-invoice") + "list-invoice".length());

        // the request URI is: /list-invoice
        // if method GET, list all invoices where CompanyId == currentUser_CompanyId
        // if method POST, TODO
        if (path.length() == 0 || path.equals("/")) {
            switch (method) {
                case "GET":
                    new ListInvoiceRR(req, res, getConnection(), companyId).serve();
                    break;
                case "POST":
                    //new ListFilteredInvoicesRR(req, res, getConnection()).serve();
                    break;
                default:
                    LOGGER.warn("Unsupported operation for URI /list-invoice: %s.", method);

                    m = new Message("Unsupported operation for URI /list-invoice.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;
    }

    /**
     * Checks whether the request is for a list of {@link Customer}s resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for a list of {@code Customer}s; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processListCustomer(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final int companyId = -1; //TODO: replace with currentUser_CompanyId, ask to autent. subgroup
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a list-customer
        if (path.lastIndexOf("rest/list-customer") <= 0) {
            return false;
        }

        // strip everything until after the /list-customer
        path = path.substring(path.lastIndexOf("list-customer") + "list-customer".length());

        // the request URI is: /list-customer
        // if method GET, list all customers where CompanyId == currentUser_CompanyId
        // if method POST, TODO
        if (path.length() == 0 || path.equals("/")) {
            switch (method) {
                case "GET":
                    new ListCustomerRR(req, res, getConnection(), companyId).serve();
                    break;
                case "POST":
                    //new ListFilteredInvoicesRR(req, res, getConnection()).serve();
                    break;
                default:
                    LOGGER.warn("Unsupported operation for URI /list-customer: %s.", method);

                    m = new Message("Unsupported operation for URI /list-customer.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;
    }

    /**
     * Checks whether the request is for a list of {@link Product}s resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for a list of {@code Product}s; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processListProduct(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final int companyId = -1; //TODO: replace with currentUser_CompanyId, ask to autent. subgroup
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a list-product
        if (path.lastIndexOf("rest/list-product") <= 0) {
            return false;
        }

        // strip everything until after the /list-product
        path = path.substring(path.lastIndexOf("list-product") + "list-product".length());

        // the request URI is: /list-product
        // if method GET, list all products where CompanyId == currentUser_CompanyId
        // if method POST, TODO
        if (path.length() == 0 || path.equals("/")) {
            switch (method) {
                case "GET":
                    new ListProductRR(req, res, getConnection(), companyId).serve();
                    break;
                case "POST":
                    //new ListFilteredInvoicesRR(req, res, getConnection()).serve();
                    break;
                default:
                    LOGGER.warn("Unsupported operation for URI /list-product: %s.", method);

                    m = new Message("Unsupported operation for URI /list-product.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;
    }


    /**
     * Checks whether the request is for a list of filtered {@link Invoice}s resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for a filtered list of {@code Invoice}s; {@code false} otherwise.
     * @throws Exception if any error occurs.
     */
    private boolean processListInvoiceByFilters(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final int companyId = -1; //TODO: replace with currentUser_CompanyId, ask to autent. subgroup
        final String method = req.getMethod();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not a filter-invoices
        if (path.lastIndexOf("rest/filter-invoices") <= 0) {
            return false;
        }

        // strip everything until after the /filter-invoices
        path = path.substring(path.lastIndexOf("filter-invoices") + "filter-invoices".length());

        List<String> filterList = List.of("filterByTotal", "fromTotal", "toTotal", "filterByDiscount", "fromDiscount", "toDiscount", "filterByPfr", "startPfr", "toPfr", "filterByInvoiceDate", "fromInvoiceDate", "toInvoiceDate", "filterByWarningDate", "fromWarningDate", "toWarningDate");
        // the request URI contains filter(s)
        boolean checkFilters = false;
        for(String filter : filterList) {
            boolean tmp = checkPath(path, filter, req, res, m);
            if(tmp)
                path = path.substring(path.lastIndexOf(filter) + filter.length());
            checkFilters = checkFilters || tmp;
        }

        // it enters here if the user doesn't fix any filter, so it should call listInvoicesByCompanyId not throw exception TODO
        if (!checkFilters) {
            LOGGER.error("## REST DISPATCHER SERVLET: ERROR IN \"if(!checkFilters)\" ##");
            m = new Message("## REST DISPATCHER SERVLET: ERROR IN \"if(!checkFilters)\" ##", "E4A8",
                    String.format("Requested URI: %s.", req.getRequestURI()));
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        }
        else {
            switch (method) {
                case "GET":
                    new ListInvoiceByFiltersRR(req, res, getConnection(), companyId).serve();
                    break;
                default:
                    LOGGER.warn("Unsupported operation for URI /employee/salary/{salary}: %s.", method);

                    m = new Message("Unsupported operation for URI /employee/salary/{salary}.", "E4A5",
                            String.format("Requested operation %s.", method));
                    res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    m.toJSON(res.getOutputStream());
                    break;
            }
        }

        return true;
    }



    private boolean checkPath(String path, String filter, HttpServletRequest req, HttpServletResponse res, Message m) throws IOException {
        try {
            if(path.contains(filter)) {
                path = path.substring(path.lastIndexOf(filter) + filter.length() + 1);
                if(path.indexOf("/") > -1)
                    path = path.substring(0, path.indexOf("/"));

                if (path.length() == 0 || path.equals("/")) {
                    LOGGER.warn("Wrong format for URI /filter-invoices/" + filter + "/{" + filter + "}: no {" + filter + "} specified. Requested URI: %s.", req.getRequestURI());

                    m = new Message("Wrong format for URI /filter-invoices/" + filter + "/{" + filter + "}: no {" + filter + "} specified.", "E4A7",
                            String.format("Requested URI: %s.", req.getRequestURI()));
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    m.toJSON(res.getOutputStream());
                    return false;
                }
            }
            else {
                return false;
            }
        } catch (IOException ex) {
            LOGGER.error("Unexpected error while processing the checkPath function.");

            m = new Message("Unexpected error in function checkPath.", "E5A1",
                    String.format("Requested URI: %s.", req.getRequestURI()));
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
            return false;
        }
        LOGGER.info("##  checkPath func: filter: " + filter + " found! Value: " + path + " ##");
        return true;
    }
}


/*
* ListFilteredInvoices

      // the request URI is: /filter-invoices/with-filters/filter1/{value_of_filter1}/filter2/{value_of_filter2}...
                if(path.contains("with-filters")) {
                    path = path.substring(path.lastIndexOf("with-filters") + "with-filters".length());

                    Map<String, Object> filtersMap = Map.of("fromTotal", null, "toTotal", null, "fromDiscount", null, "toDiscount", null, "fromPfr", null, "toPfr", null, "fromInvoiceDate", null, "toInvoiceDate", null, "fromWarningDate", null, "toWarningDate", null);
                    // the request URI contains filter(s)
                    boolean checkFilters = false;
                    for(String filter : filtersMap.keySet()) {
                        boolean tmp = checkPath(path, filter, req, res, m);
                        if(tmp) {
                            path = path.substring(path.lastIndexOf(filter) + filter.length());
                            filtersMap.put(filter, true);
                        }
                        checkFilters = checkFilters || tmp;
                    }

                    // it enters here if the user doesn't fix any filter, so it should call listInvoicesByCompanyId not throw exception TODO
                    if (!checkFilters) {
                        LOGGER.error("## REST DISPATCHER SERVLET: ERROR IN \"if(!checkFilters)\" ##");
                        m = new Message("## REST DISPATCHER SERVLET: ERROR IN \"if(!checkFilters)\" ##", "E4A8",
                                String.format("Requested URI: %s.", req.getRequestURI()));
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        m.toJSON(res.getOutputStream());
                    }
                    else {
                        switch (method) {
                            case "GET":
                                new ListInvoiceRR(req, res, getConnection()).listInvoicesByFilters(companyId, filtersMap);

                                break;
                            default:
                                LOGGER.warn("Unsupported operation for URI /employee/salary/{salary}: %s.", method);

                                m = new Message("Unsupported operation for URI /employee/salary/{salary}.", "E4A5",
                                        String.format("Requested operation %s.", method));
                                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                m.toJSON(res.getOutputStream());
                                break;
                        }
                    }
                }
            }

        }
* */

