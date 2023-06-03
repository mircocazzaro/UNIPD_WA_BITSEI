import React, {useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";
import gate from "../../../gate";
import {useParams} from "react-router-dom";
import Form from "../../../Components/Form/Form";
import {toast} from "react-toastify";

function EditProduct() {
    const [pending, setPending] = useState(false);
    const {company_id, product_id} = useParams();
    const { register, handleSubmit, reset } = useForm();

    useEffect(() => {
        setPending(true);
        gate
            .getProduct(product_id,company_id)
            .then(response => {
                const product = response.data.product;
                reset({
                    title: product.title,
                    default_price: product.default_price,
                    measurement_unit: product.measurement_unit,
                    description: product.description,
                })
                setPending(false);
            }).catch( () => {
                toast.error("Something went wrong.");
            }

        );
    }, [product_id, company_id, reset]);



    const submitHandler = (data, e) => {
        e.preventDefault();

        setPending(true);

        const formData = new FormData();
        formData.append("title", data.title);
        formData.append("default_price", data.default_price);
        formData.append("measurement_unit", data.measurement_unit);
        formData.append("description", data.description);

        console.log(formData)

        /*
        .editProduct({product: {companyID: parseInt(company_id), ...data}}, product_id, company_id)
        .editProduct({product: {companyID: parseInt(company_id), ...formData}}, product_id, company_id)
        */
        gate
            .editProduct({product: {companyID: parseInt(company_id), ...data}}, product_id, company_id)
            .then((response) => {
                console.log(response.data)
                setPending(false)
            })
            .catch((error) => {
                console.log(error)
            })
    };


    const fields = [
        [{name: "title", type: "string"}, {name: "default_price", type: "number"}],
        [{name: "measurement_unit", type: "string"}, {name: "description", type: "string"}],
    ]

    return (
        <Form title={"Product"} onSubmit={handleSubmit(submitHandler)} fields={fields} register={register}/>
    )
}

export default EditProduct;

    /*const submitHandler = (data, e) => {
            e.preventDefault();
            setPending(true);
            gate
                .editProduct({product: {companyID: parseInt(company_id), ...data}}, product_id, company_id)
                .then((response) => {
                    setPending(false)
                })
                .catch((error) => {
                    toast.error("Something went wrong.");
                })
        };


        const fields = [
            [{name: "title", type: "string"}, {name: "default_price", type: "int"}],
            [{name: "measurement_unit", type: "string"}, {name: "description", type: "string"}],
        ]

        return (
            <Form title={"Product"} onSubmit={handleSubmit(submitHandler)} fields={fields} register={register}
                  pending={pending}/>
        )
    }

export default EditProduct;*/