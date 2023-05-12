import React, {useEffect, useState} from "react";
import {useSelector, connect} from "react-redux";
import { getLists } from "../../../Store/companies/listsThunk";
import Item from "./Item/Item";
import {Link} from "react-router-dom";
import Header from "../../../Components/Header/Header";

function List({getLists}) {
    const user = useSelector((state) => state.auth.user);
    const companies = useSelector((state) => state.companies);
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
    useEffect(() => {
        getLists();
    },  [getLists]);

    return companies.pending ? ("Loading") : (
        <>
            <Header/>
        <section style={{backgroundColor: "#eee"}}>
            {/*
                TODO: add header to the page to show the user's name and avatar
            */}
            <Link className="w-full" to="/new-company">
                <button className="btn btn-outline-primary btn-sm mt-2" type="button">
                    Add Company
                </button>
            </Link>
            <div className="container py-5">
                {companies.items.map((company) => (
                    <Item id={1} img={"https://mdbcdn.b-cdn.net/img/Photos/Horizontal/E-commerce/Products/img%20(4).webp"} business_name={"business name"} title={"company 1"} details={list}/>
                ))}
                <Item id={1} img={"https://mdbcdn.b-cdn.net/img/Photos/Horizontal/E-commerce/Products/img%20(4).webp"} business_name={"business name"} title={"company 1"} details={list}/>
                <Item id={2} img={"https://mdbcdn.b-cdn.net/img/Photos/Horizontal/E-commerce/Products/img%20(4).webp"} business_name={"business name"} title={"company 1"} details={list}/>
            </div>
        </section>
        </>
    )
}

export default connect(null, {getLists})(List);