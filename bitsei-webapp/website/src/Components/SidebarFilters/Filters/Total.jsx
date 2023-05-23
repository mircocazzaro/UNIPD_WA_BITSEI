import React, {useEffect, useState} from "react";
import logo from "../../CompanyItem/bitseiLogo";
import { Form } from 'react-bootstrap';

function Total({id, name, details}) {


    const [rangeValue1, setRangeValue1] = useState(100);
    const [rangeValue2, setRangeValue2] = useState(100);
    const [checkboxChecked, setCheckboxChecked] = useState(false);

    const handleCheckboxChange = (event) => {
        console.log("handleCheckboxChange called");
        setCheckboxChecked(!checkboxChecked);
        setRangeValue1(100);
        setRangeValue2(100);
    };

    const handleRangeClick = () => {
        if (!checkboxChecked) {
          setCheckboxChecked(true);
        }
    };

    const handleRangeChange1 = (event) => {
        setRangeValue1(event.target.value);
        setCheckboxChecked(true);
    };
    const handleRangeChange2 = (event) => {
        setRangeValue2(event.target.value);
        setCheckboxChecked(true);
    };

    return (
        <form class="border border-2 rounded-4 mb-2">
            <h4 class="d-flex justify-content-start ms-2 mt-2">By Total</h4>
            <div class="container mt-4">
                <div class="row align-items-center">
                    <div class="col-auto">
                        <Form.Switch id="flexSwitchCheckDefault" checked={checkboxChecked} onChange={handleCheckboxChange}/>
                    </div>
                    <div class="col-auto">
                        <div class="row">
                            <div class="container p-0 mb-3 text-end">
                                <label for="customRange1" class="form-label" onClick={handleCheckboxChange}>From:</label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="container p-0 text-end">
                                <label for="customRange1" class="form-label">To:</label>
                            </div>
                        </div>
                    </div>
                    <div class="col">
                        <div class="row">
                            <div class="container position-relative mb-3" onClick={handleRangeClick}>
                                <Form.Label className="form-range-value position-absolute" style={{top: '-25px', left: `${(((rangeValue1-100) / (3000-100)) * 100)*0.83}%`}}>{rangeValue1}€</Form.Label>
                                <Form.Range style={{marginBottom: 0.5+'rem'}} min="100" max="3000" step="50" id="customRange1" value={rangeValue1} onChange={handleRangeChange1} disabled={!checkboxChecked}/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="container position-relative" onClick={handleRangeClick}>
                                <Form.Label className="form-range-value position-absolute" style={{top: '-25px', left: `${(((rangeValue2-100) / (3000-100)) * 100)*0.83}%`}}>{rangeValue2}€</Form.Label>
                                <Form.Range style={{marginBottom: 0.5+'rem'}} min="100" max="3000" step="50" id="customRange2" value={rangeValue2} onChange={handleRangeChange2} disabled={!checkboxChecked}/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    );
}

export default Total;