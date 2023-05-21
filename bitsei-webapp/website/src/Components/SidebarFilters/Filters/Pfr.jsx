import React, {useEffect, useState} from "react";
import logo from "../../CompanyItem/bitseiLogo";

function Pfr({id, name, details}) {

    const [checkboxChecked, setCheckboxChecked] = useState(false);

    const handleCheckboxChange = (event) => {
        setCheckboxChecked(event.target.checked);
    };

    return (
        <form class="border border-2 rounded-4 mb-2">
            <h4 class="d-flex justify-content-start ms-2 mt-2">By Pfr</h4>
            <div class="container mt-3">
                <div class="row align-items-center">
                    <div class="col-auto">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault" checked={checkboxChecked} onChange={handleCheckboxChange}></input>
                        </div>
                    </div>
                    <div class="col-auto">
                        <div class="row">
                            <div class="container p-0 mb-3 text-end">
                                <label for="customRange1" class="form-label">From:</label>
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
                            <div class="container px-4 mb-3">
                                <div class="input-group input-group-sm">
                                    <input style={{marginBottom: 0.5+'rem'}} type="text" class="form-control" placeholder="From Pfr" aria-label="From Pfr" aria-describedby="basic-addon2" disabled={!checkboxChecked}></input>
                                    <span style={{marginBottom: 0.5+'rem'}} class="input-group-text" id="basic-addon2">%</span>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="container px-4">
                                <div class="input-group input-group-sm">
                                    <input style={{marginBottom: 0.5+'rem'}} type="text" class="form-control" placeholder="To Pfr" aria-label="To Pfr" aria-describedby="basic-addon2" disabled={!checkboxChecked}></input>
                                    <span style={{marginBottom: 0.5+'rem'}} class="input-group-text" id="basic-addon2">%</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    );
}

export default Pfr;