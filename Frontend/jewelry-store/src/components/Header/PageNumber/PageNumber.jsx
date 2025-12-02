import React from "react";
import { ChevronLeft, ChevronRight } from "lucide-react"; // Đảm bảo bạn đã cài icon
import styles from "./PageNumber.module.css"; // Đảm bảo đường dẫn css đúng

// 1. Nhận props trực tiếp, KHÔNG dùng useState ở đây
function PageNumber({ currentPage, totalPages, setCurrentPage }) {

    // Generate page numbers
    const getPageNumbers = () => {
        const pages = [];
        const maxVisiblePages = 5;

        // currentPage là index (0, 1, 2...), convert sang page thực tế (1, 2, 3...) để tính toán
        const currentDisplayPage = currentPage + 1;

        if (totalPages <= maxVisiblePages) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(i);
            }
        } else {
            // Logic hiển thị dấu ...
            if (currentDisplayPage <= 3) {
                for (let i = 1; i <= 4; i++) {
                    pages.push(i);
                }
                pages.push("...");
                pages.push(totalPages);
            } else if (currentDisplayPage >= totalPages - 2) {
                pages.push(1);
                pages.push("...");
                for (let i = totalPages - 3; i <= totalPages; i++) {
                    pages.push(i);
                }
            } else {
                // Trường hợp ở giữa: 1 ... 4 5 6 ... 10
                pages.push(1);
                pages.push("...");
                pages.push(currentDisplayPage - 1); // Trang trước
                pages.push(currentDisplayPage);     // Trang hiện tại
                pages.push(currentDisplayPage + 1); // Trang sau
                pages.push("...");
                pages.push(totalPages);
            }
        }
        return pages;
    };

    const handlePageChange = (pageNumber) => {
        // pageNumber là số hiển thị (1,2,3), trừ 1 để về index (0,1,2)
        setCurrentPage(pageNumber - 1);
    };

    const handlePrev = () => setCurrentPage((prev) => Math.max(prev - 1, 0));
    const handleNext = () => setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1));

    // Nếu không có trang nào thì không render
    if (totalPages <= 1) return null;

    return (
        <div className={styles.paginationContainer}>
            <div className={styles.paginationInfo}>
                {/* Page {currentPage + 1} of {totalPages} */}
            </div>

            <div className={styles.pagination}>
                {/* Nút Previous */}
                <button
                    className={`${styles.paginationButton} ${currentPage === 0 ? styles.disabled : ""}`}
                    onClick={handlePrev}
                    disabled={currentPage === 0} // Sửa logic disable
                >
                    <ChevronLeft size={18} />
                    <span className={styles.paginationButtonText}>Previous</span>
                </button>

                {/* Danh sách số trang */}
                <div className={styles.paginationNumbers}>
                    {getPageNumbers().map((page, index) =>
                        page === "..." ? (
                            <span key={`ellipsis-${index}`} className={styles.paginationEllipsis}>
                                ...
                            </span>
                        ) : (
                            <button
                                key={page}
                                className={`${styles.paginationNumber} ${currentPage + 1 === page ? styles.activePage : ""
                                    }`}
                                onClick={() => handlePageChange(page)}
                            >
                                {page}
                            </button>
                        )
                    )}
                </div>

                {/* Nút Next */}
                <button
                    className={`${styles.paginationButton} ${currentPage === totalPages - 1 ? styles.disabled : ""
                        }`}
                    onClick={handleNext}
                    disabled={currentPage === totalPages - 1}
                >
                    <span className={styles.paginationButtonText}>Next</span>
                    <ChevronRight size={18} />
                </button>
            </div>
        </div>
    );
}

export default PageNumber;