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

import it.unipd.dei.bitsei.dao.GetCompanyDAO;
import it.unipd.dei.bitsei.dao.ListCompaniesDAO;
import it.unipd.dei.bitsei.resources.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for getting {@link Company}.
 *
 * @author BITSEI GROUP
 * @version 1.00
 * @since 1.00
 */
public final class GetCompanyRR extends AbstractRR {

    /**
     * Creates a new REST resource for listing {@code Company}s.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public GetCompanyRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.GET_COMPANIES, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        Company el = null;
        Message m = null;

        try {
            String uri = req.getRequestURI();
            String id = uri.substring(uri.lastIndexOf('/') + 1);
            if (id.isEmpty() || id.isBlank()) {
                throw new IOException("company id cannot be empty.");
            }

            int company_id = Integer.parseInt(id);
            int owner_id = Integer.parseInt(req.getSession().getAttribute("owner_id").toString());

            el = new GetCompanyDAO(con, company_id, owner_id).access().getOutputParam();

            if (el != null) {
                LOGGER.info("Company successfully get.");

                res.setStatus(HttpServletResponse.SC_OK);
                el.toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Fatal error while fetching Company.");

                m = new Message("Cannot fetch Company: make sure you have a right access to this company.", "E5A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list Companies: unexpected database error.", ex);

            m = new Message("Cannot list Companies: unexpected database error.", "E5A1", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }


}
