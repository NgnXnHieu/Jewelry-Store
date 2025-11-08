import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Client/Home/Home.jsx";
import About from "./pages/Client/About/About.jsx";
import ProductDetail from "./pages/Client/ProductDetail/ProductDetail.jsx";
import Login from "./pages/Login/Login.jsx";
import Order from "./pages/Client/Order/Order.jsx";
import Cart from "./pages/Client/Cart/Cart.jsx";
import Checkout from "./pages/Client/Checkout/Checkout.jsx";
import MainLayout from "./Layout/MainLayout.jsx";
import AuthLayout from "./Layout/AuthLayout.jsx";
import Profile from "./pages/Profile/Profile.jsx";
import AddressManager from "./pages/Client/AddressManager/AddressManager.jsx";
import Category from "./pages/Client/Category/Category.jsx";
import BestSeller from "./pages/Client/BestSeller/BestSeller.jsx";
import AdminLayout from "./Layout/AdminLayout.jsx";
import Dashboard from "./pages/Admin/Dashboard/Dashboard.jsx";
import ProductManagement from "./pages/Admin/ProductManagement/ProductManagement.jsx";
import CategoryManagement from "./pages/Admin/CategoryManagement/CategoryManagement.jsx";
import OrderManagement from "./pages/Admin/OrderManagement/OrderManagement.jsx";
import HumanResourcesManagement from "./pages/Admin/HumanResourcesManagement/HumanResourcesManagement.jsx";
import Inventory from "./pages/Admin/Inventory/Inventory.jsx";
function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Layout có Header */}
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/about" element={<About />} />
          <Route path="/productdetail/:id" element={<ProductDetail />} />
          <Route path="/order" element={<Order />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/addressManager" element={<AddressManager />} />
          <Route path="/bestSeller" element={<BestSeller />} />
          <Route path="/category/:id" element={<Category />} />
        </Route>

        {/* Layout không có Header */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<Login />} />
        </Route>

        {/* Layout cho admin */}
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="productManagement" element={< ProductManagement />} />
          <Route path="categoryManagement" element={< CategoryManagement />} />
          <Route path="orders" element={< OrderManagement />} />
          <Route path="humanResourcesManagement" element={< HumanResourcesManagement />} />
          <Route path="inventory" element={< Inventory />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;