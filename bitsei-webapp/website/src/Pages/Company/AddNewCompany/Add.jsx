import React from "react";
import {useSelector, connect} from "react-redux";
import Item from "../../../Components/CompanyItem/Item";
import {useParams} from "react-router-dom";

function AddCompany() {
    const { company_id } = useParams();
    const companies = useSelector((state) => state.companies);
    console.log(company_id)
    const list = [
        {
            "name": "sdfsd",
            "value": "Company 1",
        },
        {
            "name": "sdfsdf",
            "value": "Company 1",
        }
    ]
    // useEffect(() => {
    //     getLists();
    // },  [getLists]);

    return companies.pending ? ("Loading") : (
        <section style={{backgroundColor: "#eee"}}>
            {/*
                TODO: add header to the page to show the user's name and avatar
            */}
            <div className="container py-5">
                {/*
                    Todo: Replace this with the company data
                */}
                <Item id={1} img={"https://mdbcdn.b-cdn.net/img/Photos/Horizontal/E-commerce/Products/img%20(4).webp"} business_name={"business name"} title={"company 1"} details={list}/>
            </div>
        </section>
    )
}

export default connect(null, {})(AddCompany);