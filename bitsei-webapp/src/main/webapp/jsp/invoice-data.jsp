<!--
Copyright 2018-2023 University of Padua, Italy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Fabio Zanini (fabio.zanini@studenti.unipd.it)
Version: 1.0
Since: 1.0
-->

<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Search Invoice</title>
</head>

<body>
<h1>Search Invoice</h1>
<hr/>

<!-- display the message -->
<c:import url="/jsp/include/show-message.jsp"/>

<!-- display the found invoice, if any -->
    <table>
        <thead>
        <tr>
            <th>CUSTOMER</th><th>STATUS</th><th>WARNING NUMBER</th><th>WARNING DATE</th><th>WARNING PDF FILE</th><th>INVOICE NUMBER</th><th>INVOICE DATE</th>
        </tr>
        </thead>

        <tbody>

            <tr>
                <td><c:out value="${invoice.customer_id}"/></td>
                <td><c:out value="${invoice.status}"/></td>
                <td><c:out value="${invoice.warning_number}"/></td>
                <td><c:out value="${invoice.warning_date}"/></td>
                <td><c:out value="${invoice.warning_pdf_file}"/></td>
                <td><c:out value="${invoice.invoice_number}"/></td>
                <td><c:out value="${invoice.invoice_date}"/></td>
            </tr>
        </tbody>
    </table>

</body>
</html>
