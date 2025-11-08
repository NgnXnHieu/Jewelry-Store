import { useState, useEffect } from "react";

import styles from './HumanResourcesManagement.module.css';
import { Users, Plus, Search, Edit2, Trash2, X, Upload, ChevronLeft, ChevronRight } from 'lucide-react';
// import { Avatar, AvatarImage, AvatarFallback } from './ui/avatar';
import axiosInstance from "../../../api/axiosInstance";
import Swal from "sweetalert2";
export default function HumanResourcesManagement() {
    const [users, setUsers] = useState([])

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [itemsPerPage] = useState(5);
    const [imagePreview, setImagePreview] = useState("");
    const [fullSizeImage, setFullSizeImage] = useState(null); // For image zoom
    const [currentUser, setCurrentUser] = useState({
        id: '',
        username: '',
        email: '',
        password: '',
        role: 'Employee',
        full_name: '',
        phone: '',
        avatar: '',
        is_active: true,
        image_url: "",
        image: null
    });


    const fetchUsers = (page) => {
        axiosInstance.get(`/users/humanResources?page=${page}&size=5`)
            .then(res => {
                setUsers(res.data.content);
                setTotalPages(res.data.totalPages);
                console.log(res.data);
                console.log("API response data:", res.data.content);
                console.log("Fetched users:", users);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchUsers(currentPage);
    }, [currentPage]);

    const handleOpenModal = (user = null) => {
        if (user) {
            setCurrentUser(user);
            setIsEditing(true);
        } else {
            setCurrentUser({
                username: '',
                email: '',
                password: '',
                role: 'Employee',
                full_name: '',
                phone: '',
                avatar: '',
                is_active: true,
                image_url: "",
                image: null
            });
            setIsEditing(false);
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentUser({
            username: '',
            email: '',
            password: '',
            role: 'Employee',
            full_name: '',
            phone: '',
            avatar: '',
            is_active: true,
            image_url: "",
            image: null
        });
        setImagePreview("")
    };

    //Xử lý lưu và sửa
    const handleSubmit = async (e) => {
        //Ngăn chặn hành vi mặc định là reload trang
        e.preventDefault();
        const result = await Swal.fire({
            title: isEditing ? `Bạn có chắc muốn cập nhật thông tin người dùng ID: ${currentUser.id} ?` : `Bạn có chắc muốn tạo người dùng mới ?`,
            icon: 'question',
            timer: 5000,
            timerProgressBar: true,
            showCancelButton: true,
            confirmButtonText: 'Chắc chắn',
            cancelButtonText: 'Hủy'
        });
        if (!result.isConfirmed) {
            return;
        }

        if (isEditing) {
            // Update user - stay on current page
            const formData = new FormData();
            formData.append("full_name", (currentUser.full_name));
            formData.append("email", (currentUser.email));
            formData.append("is_active", currentUser.is_active);
            formData.append("phone", currentUser.phone);
            formData.append("role", currentUser.role);
            formData.append("image", currentUser.image)
            // if (form.image) {
            //     formData.append("image", form.image);
            // } else if (form.image_url) {
            //     formData.append("image_url", form.image_url);
            // }
            // console.log(res.data);
            // console.log("API response data:", res.data.content);
            // console.log("Fetched users:", users);
            await axiosInstance.put(`/users/${currentUser.id}`, formData).then(res => {

            })
                .catch(err => console.error(err));
            fetchUsers(currentPage);
            Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: `Người dùng đã được cập nhật thành công.`
            });

        } else {
            // Create new user - go to first page
            const formData = new FormData();
            formData.append("username", currentUser.username);
            formData.append("full_name", (currentUser.full_name));
            formData.append("email", (currentUser.email));
            formData.append("is_active", currentUser.is_active);
            formData.append("phone", currentUser.phone);
            formData.append("role", currentUser.role);
            formData.append("password", currentUser.password);
            formData.append("image", currentUser.image)

            // if (form.image) {
            //     formData.append("image", form.image);
            // } else if (form.image_url) {
            //     formData.append("image_url", form.image_url);
            // }
            // console.log(res.data);
            // console.log("API response data:", res.data.content);
            // console.log("Fetched users:", users);
            await axiosInstance.post(`/users`, formData).then(res => {

            })
                .catch(err => console.error(err));
            setCurrentPage(0);
            fetchUsers(0);
            Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: `Người dùng đã được tạo thành công.`
            });
        }

        handleCloseModal();
    };

    const handleDelete = (id) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            setUsers(users.filter(user => user.id !== id));
            // Stay on current page after delete
            // Adjust page if needed (if current page becomes empty)
            const filteredCount = users.filter(user => user.id !== id).length;
            const maxPage = Math.ceil(filteredCount / itemsPerPage);
            if (currentPage > maxPage && maxPage > 0) {
                setCurrentPage(maxPage);
            }
        }
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setCurrentUser({ ...currentUser, [name]: type === 'checkbox' ? checked : value });
    };

    // const filteredUsers = users.filter(user =>
    //     // user.full_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    //     user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    //     user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    //     user.role.toLowerCase().includes(searchTerm.toLowerCase())
    // );

    // Pagination calculations
    // const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
    const [totalPages, setTotalPages] = useState(0);
    // const indexOfLastUser = currentPage * itemsPerPage;
    // const indexOfFirstUser = indexOfLastUser - itemsPerPage;
    // const currentUsers = filteredUsers.slice(indexOfFirstUser, indexOfLastUser);


    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber - 1);
    };

    // const handlePreviousPage = () => {
    //     if (currentPage > 1) {
    //         setCurrentPage(currentPage - 1);
    //     }
    // };

    // const handleNextPage = () => {
    //     if (currentPage < totalPages) {
    //         setCurrentPage(currentPage + 1);
    //     }
    // };

    const handlePrev = () => setCurrentPage(prev => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));


    // Reset to page 1 when search term changes
    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setCurrentPage(1);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    // Generate page numbers for pagination
    const getPageNumbers = () => {
        const pages = [];
        const maxVisiblePages = 5;

        if (totalPages <= maxVisiblePages) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(i);
            }
        } else {
            if (currentPage <= 3) {
                for (let i = 1; i <= 4; i++) {
                    pages.push(i);
                }
                pages.push('...');
                pages.push(totalPages);
            } else if (currentPage >= totalPages - 2) {
                pages.push(1);
                pages.push('...');
                for (let i = totalPages - 3; i <= totalPages; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                pages.push('...');
                pages.push(currentPage - 1);
                pages.push(currentPage);
                pages.push(currentPage + 1);
                pages.push('...');
                pages.push(totalPages);
            }
        }

        return pages;
    };

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <div className={styles.headerContent}>
                    <div className={styles.headerTitle}>
                        <Users className={styles.headerIcon} />
                        <div>
                            <h1>Human Resource Management</h1>
                            <p>Manage your team members and their information</p>
                        </div>
                    </div>
                    <button className={styles.addButton} onClick={() => handleOpenModal()}>
                        <Plus size={20} />
                        Add User
                    </button>
                </div>
            </div>

            <div className={styles.content}>
                <div className={styles.searchBar}>
                    <Search className={styles.searchIcon} />
                    <input
                        type="text"
                        placeholder="Search by name, email, username or role..."
                        value={searchTerm}
                        onChange={handleSearchChange}
                        className={styles.searchInput}
                    />
                </div>

                <div className={styles.tableContainer}>
                    <table className={styles.table}>
                        <thead>
                            <tr>
                                <th className={styles.stickyColId}>ID</th>
                                <th className={styles.stickyColName}>Full Name</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th>Active</th>
                                <th>Created At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td className={styles.stickyColId}>{user.id}</td>
                                    <td className={styles.stickyColName}>
                                        <div className={styles.userCell}>
                                            <div className={styles.imageCell}>
                                                {user.image_url ? (
                                                    <img
                                                        src={`http://localhost:8080/images/${user.image_url}`}
                                                        alt={user.name}
                                                        className={styles.userImage}
                                                        onClick={() => setFullSizeImage({ url: `http://localhost:8080/images/${user.image_url}`, name: user.name })}
                                                        style={{ cursor: 'pointer' }}
                                                    />
                                                ) : (
                                                    <div className={styles.noImage}>
                                                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                                        </svg>
                                                    </div>
                                                )}
                                            </div>
                                            <span>{user.full_name}</span>
                                        </div>
                                    </td>
                                    <td>{user.username}</td>
                                    <td>{user.email}</td>
                                    <td>{user.phone}</td>
                                    <td>
                                        {/* <span className={`${styles.badge} ${styles[user.role.toLowerCase()]}`}> */}
                                        <span className={`${styles.badge} ${"hihi"}`}>
                                            {user.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span className={`${styles.statusBadge} ${user.is_active ? styles.active : styles.inactive}`}>
                                            {user.is_active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>{formatDate(user.created_at)}</td>
                                    <td>
                                        <div className={styles.actions}>
                                            <button
                                                className={styles.actionButton}
                                                onClick={() => handleOpenModal(user)}
                                                title="Edit"
                                            >
                                                <Edit2 size={16} />
                                            </button>
                                            <button
                                                className={`${styles.actionButton} ${styles.deleteButton}`}
                                                onClick={() => handleDelete(user.id)}
                                                title="Delete"
                                            >
                                                <Trash2 size={16} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {/* {filteredUsers.length === 0 && (
                        <div className={styles.noResults}>
                            <Users size={48} />
                            <p>No users found</p>
                            <span>Try adjusting your search or add a new user</span>
                        </div>
                    )} */}
                </div>



                {/* Generate page numbers for pagination */}
                <div className={styles.paginationContainer}>
                    <div className={styles.paginationInfo}>
                        The Page {currentPage + 1} of {totalPages}
                        {/* Showing {indexOfFirstUser + 1} to {Math.min(indexOfLastUser, filteredUsers.length)} of {filteredUsers.length} users */}
                    </div>
                    <div className={styles.pagination}>
                        {/* Nút quay lại trang trước */}
                        <button
                            className={`${styles.paginationButton} ${currentPage === 0 ? styles.disabled : ''}`}
                            // onClick={handlePreviousPage}
                            onClick={handlePrev}
                            disabled={currentPage + 1 === 0}

                        >
                            <ChevronLeft size={18} />
                            <span className={styles.paginationButtonText}>Previous</span>
                        </button>

                        <div className={styles.paginationNumbers}>
                            {getPageNumbers().map((page, index) => (
                                page === '...' ? (
                                    <span key={`ellipsis-${index}`} className={styles.paginationEllipsis}>
                                        ...
                                    </span>
                                ) : (
                                    <button
                                        key={page}
                                        className={`${styles.paginationNumber} ${currentPage + 1 === page ? styles.activePage : ''}`}
                                        // onClick={() => handlePageChange(page)}
                                        onClick={() => handlePageChange(page)}
                                    >
                                        {page}
                                    </button>
                                )
                            ))}
                        </div>
                        {/* Nút chuyển trang kế tiếp */}
                        <button
                            className={`${styles.paginationButton} ${currentPage + 1 === totalPages ? styles.disabled : ''}`}
                            onClick={handleNext}
                            // onClick={handleNextPage}
                            disabled={currentPage === totalPages - 1}
                        >
                            <span className={styles.paginationButtonText}>Next</span>
                            <ChevronRight size={18} />
                        </button>
                    </div>
                </div>
            </div>

            {isModalOpen && (
                <div className={styles.modalOverlay} onClick={handleCloseModal}>
                    <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <div className={styles.modalHeader}>
                            <h2>{isEditing ? 'Sửa thông tin' : 'Thêm nhân sự'}</h2>
                            <button className={styles.closeButton} onClick={handleCloseModal}>
                                <X size={24} />
                            </button>
                        </div>

                        <form onSubmit={handleSubmit}>
                            <div className={styles.avatarUploadSection}>
                                <div className={styles.avatarPreview}>
                                    {/* <Avatar className={styles.avatarLarge}>
                                        <AvatarImage src={currentUser.avatar} alt={currentUser.full_name || 'User'} />
                                        <AvatarFallback className={styles.avatarFallback}> */}
                                    {currentUser.full_name ? currentUser.full_name.charAt(0) : <Upload size={24} />}
                                    {/* </AvatarFallback>
                                    </Avatar> */}
                                </div>
                                <div className={styles.formGroup} style={{ flex: 1 }}>
                                    <div className={styles.imageBox}>
                                        {imagePreview || currentUser.image_url ? (
                                            <div className={styles.imagePreviewWrapper}>
                                                {/* Hiển thị ảnh nếu tồn tại */}
                                                <img
                                                    src={imagePreview || `http://localhost:8080/images/${currentUser.image_url}`}
                                                    alt="Preview"
                                                    className={styles.imagePreview}
                                                />
                                                {/* Nút gỡ ảnh */}
                                                <button
                                                    type="button"
                                                    className={styles.removeImageBtn}
                                                    onClick={() => {
                                                        if (imagePreview) {
                                                            URL.revokeObjectURL(imagePreview);
                                                        }
                                                        setImagePreview("");
                                                        setCurrentUser({ ...currentUser, image: null, image_url: "" });
                                                    }}
                                                >
                                                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                                    </svg>
                                                </button>
                                            </div>
                                        ) : (
                                            // Hiển thị khi chưa có ảnh
                                            <label htmlFor="fileInput" className={styles.uploadLabel}>
                                                <svg className={styles.uploadIcon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                                                </svg>
                                                <span>Tải ảnh lên</span>
                                                <span className={styles.uploadHint}>hoặc kéo thả ảnh vào đây</span>
                                            </label>
                                        )}
                                    </div>

                                    {/* Input của ảnh */}
                                    <input
                                        id="fileInput"
                                        type="file"
                                        accept="image/*"
                                        className={styles.fileInput}
                                        onChange={(e) => {
                                            //Lấy ra file đầu tiên
                                            const file = e.target.files[0];
                                            //Kiểm tra file có tồn tại không 
                                            if (file) {
                                                // 1️⃣ Kiểm tra định dạng file
                                                const validTypes = ["image/jpeg", "image/png", "image/jpg", "image/gif"];
                                                if (!validTypes.includes(file.type)) {
                                                    Swal.fire({
                                                        icon: "error",
                                                        title: "Định dạng không hợp lệ",
                                                        text: "Vui lòng chọn ảnh JPG, JPEG, PNG hoặc GIF",
                                                    });
                                                    e.target.value = ""; // reset input
                                                    return;
                                                }

                                                // 2️⃣ Kiểm tra kích thước file (ví dụ < 2MB)
                                                const maxSize = 10 * 1024 * 1024; // 2MB
                                                if (file.size > maxSize) {
                                                    Swal.fire({
                                                        icon: "error",
                                                        title: "Kích thước ảnh quá lớn",
                                                        text: "Vui lòng chọn ảnh nhỏ hơn 10MB",
                                                    });
                                                    e.target.value = "";
                                                    return;
                                                }
                                                //Nếu đã có file trước đó được chọn thì xóa đi rồi set file mới
                                                if (imagePreview) {
                                                    console.log("image:", imagePreview);

                                                    URL.revokeObjectURL(imagePreview);
                                                }
                                                setImagePreview(URL.createObjectURL(file));
                                                setCurrentUser({
                                                    ...currentUser,
                                                    image: file
                                                    // image_url: "",
                                                });
                                                console.log("image:", imagePreview);

                                                console.log("file: ", currentUser.image);
                                            }
                                            console.log("image:", imagePreview);
                                        }}
                                    />
                                    {/* <label htmlFor="avatar">Avatar URL</label>
                                    <input
                                        type="text"
                                        id="avatar"
                                        name="avatar"
                                        value={currentUser.image_url}
                                        onChange={(e) => {
                                            //Nếu chưa có file ảnh được up thì mới được dán link
                                            if (imagePreview.image == null) {
                                                setForm({
                                                    ...form,
                                                    image_url: e.target.value,
                                                });
                                            }
                                        }}
                                        placeholder="Enter image URL (optional)"
                                    /> */}
                                    <span className={styles.helpText}>Leave empty for default avatar with initials</span>
                                </div>

                            </div>

                            <div className={styles.formGrid}>
                                <div className={styles.formGroup}>
                                    <label htmlFor="full_name">Full Name *</label>
                                    <input
                                        type="text"
                                        id="full_name"
                                        name="full_name"
                                        value={currentUser.full_name}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Enter full name"
                                    />
                                </div>

                                <div className={styles.formGroup}>
                                    <label htmlFor="username">Username *</label>
                                    {!isEditing ? (
                                        <input
                                            type="text"
                                            id="username"
                                            name="username"
                                            value={currentUser.username}
                                            onChange={handleInputChange}
                                            required
                                            placeholder="Enter username"
                                        />) : (<span className={styles.lableUsername}><b>{currentUser.email}</b></span>)
                                    }
                                </div>

                                <div className={styles.formGroup}>
                                    <label htmlFor="email">Email *</label>
                                    <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        value={currentUser.email}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Enter email address"
                                    />
                                </div>

                                <div className={styles.formGroup}>
                                    <label htmlFor="phone">Phone *</label>
                                    <input
                                        type="tel"
                                        id="phone"
                                        name="phone"
                                        value={currentUser.phone}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Enter phone number"
                                    />
                                </div>

                                {!isEditing && (
                                    <div className={styles.formGroup}>
                                        <label htmlFor="password">Password *</label>
                                        <input
                                            type="password"
                                            id="password"
                                            name="password"
                                            value={currentUser.password}
                                            onChange={handleInputChange}
                                            required
                                            placeholder="Enter password"
                                            autocomplete="new-password"
                                        />
                                    </div>
                                )}

                                <div className={styles.formGroup}>
                                    <label htmlFor="role">Role *</label>
                                    <select
                                        id="role"
                                        name="role"
                                        value={currentUser.role}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="Employee">Employee</option>
                                        <option value="Manager">Manager</option>
                                        <option value="HR">HR</option>
                                        <option value="Admin">Admin</option>
                                    </select>
                                </div>

                                <div className={styles.formGroup}>
                                    <label htmlFor="active" className={styles.checkboxLabel}>
                                        <input
                                            type="checkbox"
                                            id="active"
                                            name="is_active"
                                            checked={currentUser.is_active}
                                            onChange={handleInputChange}
                                            className={styles.checkbox}
                                        />
                                        <span>Active</span>
                                    </label>
                                </div>
                            </div>

                            <div className={styles.modalActions}>
                                <button
                                    type="button"
                                    className={styles.cancelButton}
                                    onClick={handleCloseModal}
                                >
                                    Cancel
                                </button>
                                <button type="submit" className={styles.submitButton}>
                                    {isEditing ? 'Update User' : 'Create User'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
            {/* Full Size Image Viewer */}
            {fullSizeImage && (
                <div
                    className={styles.imageViewerOverlay}
                    onClick={() => setFullSizeImage(null)}
                >
                    <div className={styles.imageViewerContainer}>
                        <button
                            className={styles.imageViewerClose}
                            onClick={() => setFullSizeImage(null)}
                        >
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                        <img
                            src={fullSizeImage.url}
                            alt={fullSizeImage.name}
                            className={styles.imageViewerImage}
                            onClick={(e) => e.stopPropagation()}
                        />
                        <div className={styles.imageViewerLabel}>{fullSizeImage.name}</div>
                    </div>
                </div>
            )}
        </div>
    );
}
