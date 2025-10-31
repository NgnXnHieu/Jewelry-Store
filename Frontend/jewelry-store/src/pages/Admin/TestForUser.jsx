import { useState } from "react";
import axiosInstance from "../../api/axiosInstance";

export default function TestForAdmin() {


    return (
        <div style={{ padding: "20px" }}>
            <h2>Test /api/forUser (USER Role)</h2>
            <button>Call API</button>
            <p>Response: {responseMessage}</p>
        </div>
    );
}
