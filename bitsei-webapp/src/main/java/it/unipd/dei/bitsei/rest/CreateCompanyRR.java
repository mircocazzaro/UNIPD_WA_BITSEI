/*
 * Copyright 2022-2023 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.unipd.dei.bitsei.rest;

import it.unipd.dei.bitsei.dao.CreateCompanyDAO;
import it.unipd.dei.bitsei.dao.ListUserDAO;
import it.unipd.dei.bitsei.resources.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for creating a new {@link Company}.
 *
 * @author BITSEI GROUP
 * @version 1.00
 * @since 1.00
 */
public final class CreateCompanyRR extends AbstractRR {

    /**
     * Creates a new REST resource for listing {@code User}s.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CreateCompanyRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.CREATE_COMPANY, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        Company c = null;
        Message m = null;

        try {
            final Company company = Company.fromJSON(req.getInputStream());
            int owner_id = Integer.parseInt(req.getSession().getAttribute("owner_id").toString());
            // creates a new DAO for accessing the database and creates the company
            c = new CreateCompanyDAO(con, owner_id, company).access().getOutputParam();

            if (c != null) {
                LOGGER.info("Company created successfully.");

                res.setStatus(HttpServletResponse.SC_OK);
                c.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while creating company.");

                m = new Message("Cannot create company: you reach your plan limitations.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot create company: unexpected database error.", ex);

            m = new Message("Cannot create company: unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }


}
