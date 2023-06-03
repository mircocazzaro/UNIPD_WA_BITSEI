import React, {useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";
import {toast} from "react-toastify";
import gate from "../../../gate";
import Form  from "../../../Components/Form/Form";
import {useParams} from "react-router-dom";
import {history} from "../../../index";


function EditBankAccount() {
    const [pending, setPending] = useState(false);
    const {company_id, bankaccount_id} = useParams();
    const { register, handleSubmit, reset, formState:{errors}} = useForm();


    useEffect(() => {
        setPending(true);
        gate
            .getBankAccount(bankaccount_id, company_id)
            .then(response => {
                console.log(response);
                const bankAccount = response.data;
                reset({
                    IBAN: bankAccount.IBAN,
                    bank_name: bankAccount.bank_name,
                    bankaccount_friendly_name: bankAccount.bankaccount_friendly_name,
                    company_id: bankAccount.company_id,
                })
                setPending(false);
                
            }).catch( () => {
                toast.error("Something went wrong.");}
            );
    }, []);


    
    const submitHandler = (data, e) => {
        e.preventDefault();

        setPending(true);

        console.log(data);

        gate
            .editBankAccount(  {...data},bankaccount_id, company_id)
            .then((response) => {
                console.log(response.data)
                setPending(false)
                history.push(`/companies/${company_id}/bankAccount`);
            })
            .catch((error) => {
                console.log(error)
            })

        };

    const fields = [
        [{
            value: "IBAN", name: "IBAN", type: "string", options: {
                required: "Required",
                minlength: 3,
                message: "Please enter a IBAN",
            }
        }],

        [{
            value: "Bank Name", name: "bank_name", type: "string", options: {
                minlength: 3,
                message: "Please enter a bank name",
            }
        }],

        [{
            value: "Bank Account Friendly Name", name: "bankaccount_friendly_name", type: "string", options: {
                required: "Required",
                minlength: 3,
                message: "Please enter a frendly name",
            }
        }],

    ]
    


    return (
        
        <Form title={"Edit Bank Account"} onSubmit={handleSubmit(submitHandler)} fields={fields} register={register} errors={errors} pending={pending}/>
        
        
    )
}

export default EditBankAccount;