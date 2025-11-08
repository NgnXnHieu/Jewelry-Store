import Header from "../components/Header/Header";
import { Outlet } from "react-router-dom";
import style from "./MainLayout.module.css";

function MainLayout() {
    return (
        <div className={style.mainLayout}>
            <Header />
            <main className={style.mainContent}>
                <Outlet />
            </main>
        </div>
    );
}

export default MainLayout;
