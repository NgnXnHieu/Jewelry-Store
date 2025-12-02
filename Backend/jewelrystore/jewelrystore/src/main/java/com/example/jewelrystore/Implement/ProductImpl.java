package com.example.jewelrystore.Implement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.BestSellerDTO;
import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.Entity.Category;
import com.example.jewelrystore.Entity.Inventory_History;
import com.example.jewelrystore.Entity.Order_Detail;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.ProductForm.ProductCreateForm;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Mapper.ProductMapper;
import com.example.jewelrystore.Repository.CategoryRepository;
import com.example.jewelrystore.Repository.Order_DetailRepository;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.ProductService;

import net.coobird.thumbnailator.Thumbnails;

@Service

public class ProductImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Order_DetailRepository order_DetailRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // @Override
    // public ProductDTO createProduct(ProductCreateForm Form, MultipartFile image)
    // {
    // String imagePath = null;
    // String image_url = Form.getImage_url();
    // try {
    // if (image != null && !image.isEmpty()) {
    // // Lưu file vào thư mục server, ví dụ: "uploads/"
    // String uploadDir = "D:\\DACN\\Picture";
    // // Lưu dưới dạng thời gian + tên file để tránh trùng tên
    // String fileName = System.currentTimeMillis() + "_" +
    // image.getOriginalFilename();
    // // Tạo đường dẫn đầy đủ tối file
    // Path filePath = Paths.get(uploadDir, fileName);
    // // Tạo các thư mục cha nếu chưa tồn tại
    // Files.createDirectories(filePath.getParent());
    // // Lưu file vào ổ cứng
    // Files.copy(image.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);
    // imagePath = fileName; // lưu tên file vào database
    // } else if (image_url != null && !image_url.isEmpty()) {
    // imagePath = image_url; // dùng link trực tiếp
    // }

    // // Tạo Product entity
    // Product product = productMapper
    // .toEntity(Form);
    // product.setImage_url(imagePath);

    // productRepository.save(product);

    // return productMapper.toProductDTO(product);

    // } catch (IOException e) {
    // e.printStackTrace();
    // return null;
    // }

    // }

    @Override
    public ProductDTO createProduct(ProductCreateForm Form, MultipartFile image, String username) {
        String imagePath = null;
        String image_url = Form.getImage_url();

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = "D:\\DACN\\Picture";
                Files.createDirectories(Paths.get(uploadDir));

                // Tên file: thời gian + tên gốc
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                // ✅ Resize ảnh về kích thước 500x500 và lưu xuống
                Thumbnails.of(image.getInputStream())
                        .size(1400, 1400)
                        .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                        .outputQuality(1) // giảm dung lượng, 85% chất lượng
                        .toFile(filePath.toFile());

                imagePath = fileName;

            } else if (image_url != null && !image_url.isEmpty()) {
                imagePath = image_url; // dùng link nếu có
            }

            // 🔹 Tạo entity và lưu DB
            Product product = productMapper.toEntity(Form);
            product.setImage_url(imagePath);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Inventory_History inventory_History = new Inventory_History();
            inventory_History.setUser(user);
            Long quantity = Form.getQuantity();
            inventory_History.setImportQuantity(quantity);
            inventory_History.setCurrentQuantity(quantity);
            inventory_History.setProduct(product);
            List<Inventory_History> histories = new ArrayList<Inventory_History>();
            histories.add(inventory_History);
            product.setInventory_Histories(histories);
            productRepository.save(product);
            return productMapper.toProductDTO(product);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
        }
    }

    @Override
    public ProductDTO updateProduct(Integer id, ProductUpdateForm Form, MultipartFile image) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            productMapper.updateProduct(Form, existing);
            String imagePath = null;
            String image_url = Form.getImage_url();

            try {
                if (image != null && !image.isEmpty()) {
                    String uploadDir = "D:\\DACN\\Picture";
                    Files.createDirectories(Paths.get(uploadDir));

                    // Tên file: thời gian + tên gốc
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // ✅ Resize ảnh về kích thước 700x700 và lưu xuống
                    Thumbnails.of(image.getInputStream())
                            .size(1400, 1400)
                            .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                            .outputQuality(1) // giảm dung lượng, 85% chất lượng
                            .toFile(filePath.toFile());

                    imagePath = fileName;

                } else if (image_url != null && !image_url.isEmpty()) {
                    imagePath = image_url; // dùng link nếu có
                }

                // Lưu lại sản phẩm
                if (imagePath != null && !imagePath.isEmpty()) {
                    existing.setImage_url(imagePath);
                }
                existing.setImage_url(imagePath);
                productRepository.save(existing);
                return productMapper.toProductDTO(existing);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
            }
        }
        return null;

    }

    @Override
    public Page<ProductDTO> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toProductDTO);
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        return productRepository.findById(id).map(productMapper::toProductDTO).orElse(null);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> getRelatedProducts(Integer id, Pageable pageable) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Integer categoryId = product.getCategory().getId();

        Page<Product> relatedProducts = productRepository.findByCategoryIdAndIdNot(categoryId, id, pageable);

        return relatedProducts.map(productMapper::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategoryId(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(productMapper::toProductDTO);
    }

    @Override
    public Page<BestSellerDTO> getBestSellerProduct(Pageable pageable) {
        return productRepository.findBestSellerProducts(pageable);
    }

    @Override
    public Page<ProductDTO> getProductsByStockStatus(String status, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        if (status.equalsIgnoreCase("In")) {
            return productRepository.findByQuantityGreaterThan(0L, sortedPageable).map(productMapper::toProductDTO);
        } else if (status.equalsIgnoreCase("Low")) {
            return productRepository.findByQuantityGreaterThanAndQuantityLessThan(0L, 21L, sortedPageable)
                    .map(productMapper::toProductDTO);
        } else {
            return productRepository.findByQuantity(0L, sortedPageable).map(productMapper::toProductDTO);
        }

    }

    @Override
    public Long getTotalQuantity() {
        return productRepository.getTotalQuantity();

    }

    @Override
    public Long countProductByQuantityBetween(Long min, Long max) {
        return productRepository.countProductByQuantityBetween(min, max);

    }

    @Override
    public Long countProductByQuantityBySpecificQuantity(Long quantity) {
        return productRepository.countProductByQuantityBySpecificQuantity(quantity);
    }

    @Override
    public Long countProductByQuantityGreaterSpecificQuantity(Long quantity) {
        return productRepository.countProductByQuantityGreaterSpecificQuantity(quantity);
    }

    @Override
    public Long getCountAllProducts() {
        return productRepository.countAllProducts();
    }

    @Override
    public Long getCountInProducts() {
        return productRepository.countProductByQuantityGreaterSpecificQuantity(0L);
    }

    @Override
    public Long getCountOutProducts() {
        return productRepository.countProductByQuantityBySpecificQuantity(0L);
    }

    // Trả về số lượng hiện tại của các trạng thái sản phẩm
    @Override
    public Map<String, Long> getStockStats(Map<String, Long> body) {
        // nếu null thì trả về 0, còn nếu không thì lấy giá trị mặc định
        Long all = Objects.requireNonNullElse(productRepository.getTotalQuantity(), 0L);
        Long low = Objects.requireNonNullElse(
                productRepository.countProductByQuantityBetween(body.get("minOfLow"), body.get("maxOfLow")), 0L);
        Long out = Objects.requireNonNullElse(
                productRepository.countProductByQuantityBySpecificQuantity(body.get("out")),
                0L);
        Long in = Objects.requireNonNullElse(
                productRepository.countProductByQuantityGreaterSpecificQuantity(body.get("in")),
                0L);
        Long countAllProducts = Objects.requireNonNullElse(productRepository.countAllProducts(), 0L);
        // Các phần tử trong Map phải trùng tên với biến bên frontend nhận
        Map<String, Long> result = new HashMap<>();
        result.put("totalProducts", all);
        result.put("lowStockCount", low);
        result.put("outOfStockCount", out);
        result.put("inStockCount", in);
        result.put("totalUnits", countAllProducts);
        // System.out.println("in result: " + result.get("outOfStockCount"));
        // System.out.println("in out: " + out);
        return result;
    }

    @Override
    public Map<String, Object> getBestSellerProductByUnitTime(String time) {
        if (time.equalsIgnoreCase("year")) {
            try {
                int thisYear = LocalDate.now().getYear();
                LocalDateTime startDateTime = LocalDate.of(thisYear, 1, 1).atStartOfDay();
                LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Product, Long> productsTotal = new HashMap<>();
                for (Order_Detail od : order_Details) {
                    Product product = od.getProduct();
                    // Lấy giá trị của key(nếu key chưa tồn tại trả về mặc định 0)
                    productsTotal.put(product, productsTotal.getOrDefault(product, 0L) + od.getQuantity());
                }
                Product product = productsTotal.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(null);

                Map<String, Object> result = new HashMap<>();
                result.put("producId", product.getId());
                result.put("productName", product.getName());
                result.put("sellQuantity", productsTotal.get(product));
                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        } else if (time.equalsIgnoreCase("day")) {
            try {
                LocalDateTime startDateTime = LocalDate.now().atStartOfDay();
                LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Product, Long> productsTotal = new HashMap<>();
                for (Order_Detail od : order_Details) {
                    Product product = od.getProduct();
                    // Lấy giá trị của key(nếu key chưa tồn tại trả về mặc định 0)
                    productsTotal.put(product, productsTotal.getOrDefault(product, 0L) + od.getQuantity());
                }
                Product product = productsTotal.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(null);

                Map<String, Object> result = new HashMap<>();
                result.put("producId", product.getId());
                result.put("productName", product.getName());
                result.put("sellQuantity", productsTotal.get(product));
                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        } else {
            try {
                YearMonth yearMonth = YearMonth.now();
                LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
                LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Product, Long> productsTotal = new HashMap<>();
                for (Order_Detail od : order_Details) {
                    Product product = od.getProduct();
                    // Lấy giá trị của key(nếu key chưa tồn tại trả về mặc định 0)
                    productsTotal.put(product, productsTotal.getOrDefault(product, 0L) + od.getQuantity());
                }
                Product product = productsTotal.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(null);

                Map<String, Object> result = new HashMap<>();
                result.put("producId", product.getId());
                result.put("productName", product.getName());
                result.put("sellQuantity", productsTotal.get(product));
                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getTopAndBotSellingCategories(String time) {
        if (time.equalsIgnoreCase("year")) {
            try {
                int thisYear = LocalDate.now().getYear();
                LocalDateTime startDateTime = LocalDate.of(thisYear, 1, 1).atStartOfDay();
                LocalDateTime endDateTime = LocalDateTime.of(thisYear, 12, 31, 23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Category, Long> selling = new HashMap<>();
                List<Category> categories = categoryRepository.findAll();
                for (Category category : categories) {
                    Integer id = category.getId();
                    long quantity = 0;
                    Iterator<Order_Detail> it = order_Details.iterator();

                    while (it.hasNext()) {
                        Order_Detail o = it.next();
                        if (o.getProduct().getCategory().getId().equals(id)) {
                            quantity += o.getQuantity();
                            it.remove();
                        }
                    }
                    selling.put(category, quantity);
                }

                Map.Entry<Category, Long> maxEntry = selling.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

                Map.Entry<Category, Long> minEntry = selling.entrySet()
                        .stream()
                        .min(Map.Entry.comparingByValue())
                        .orElse(null);
                Map<String, Object> maxCategory = new HashMap();
                maxCategory.put("categoryId", maxEntry.getKey().getId());
                maxCategory.put("categoryName", maxEntry.getKey().getName());
                maxCategory.put("quantity", maxEntry.getValue());
                Map<String, Object> minCategory = new HashMap();
                minCategory.put("categoryId", minEntry.getKey().getId());
                minCategory.put("categoryName", minEntry.getKey().getName());
                minCategory.put("quantity", minEntry.getValue());
                Map<String, Object> result = new HashMap<>();
                result.put("maxCategory", maxCategory);
                result.put("minCategory", minCategory);

                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        } else if (time.equalsIgnoreCase("day")) {
            try {
                LocalDateTime startDateTime = LocalDate.now().atStartOfDay();
                LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Category, Long> selling = new HashMap<>();
                List<Category> categories = categoryRepository.findAll();
                int n = categories.size();
                for (Category category : categories) {
                    Integer id = category.getId();
                    long quantity = 0;
                    Iterator<Order_Detail> it = order_Details.iterator();

                    while (it.hasNext()) {
                        Order_Detail o = it.next();
                        if (o.getProduct().getCategory().getId().equals(id)) {
                            quantity += o.getQuantity();
                            it.remove();
                        }
                    }
                    selling.put(category, quantity);
                }

                Map.Entry<Category, Long> maxEntry = selling.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

                Map.Entry<Category, Long> minEntry = selling.entrySet()
                        .stream()
                        .min(Map.Entry.comparingByValue())
                        .orElse(null);

                Map<String, Object> maxCategory = new HashMap();
                maxCategory.put("categoryId", maxEntry.getKey().getId());
                maxCategory.put("categoryName", maxEntry.getKey().getName());
                maxCategory.put("quantity", maxEntry.getValue());
                Map<String, Object> minCategory = new HashMap();
                minCategory.put("categoryId", minEntry.getKey().getId());
                minCategory.put("categoryName", minEntry.getKey().getName());
                minCategory.put("quantity", minEntry.getValue());
                Map<String, Object> result = new HashMap<>();
                result.put("maxCategory", maxCategory);
                result.put("minCategory", minCategory);
                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        } else {
            try {
                YearMonth yearMonth = YearMonth.now();
                LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
                LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                List<Order_Detail> order_Details = order_DetailRepository.findByOrder_OrderDateBetween(startDateTime,
                        endDateTime);
                Map<Category, Long> selling = new HashMap<>();
                List<Category> categories = categoryRepository.findAll();
                int n = categories.size();
                for (Category category : categories) {
                    Integer id = category.getId();
                    long quantity = 0;
                    Iterator<Order_Detail> it = order_Details.iterator();

                    while (it.hasNext()) {
                        Order_Detail o = it.next();
                        if (o.getProduct().getCategory().getId().equals(id)) {
                            quantity += o.getQuantity();
                            it.remove();
                        }
                    }
                    selling.put(category, quantity);
                }

                Map.Entry<Category, Long> maxEntry = selling.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

                Map.Entry<Category, Long> minEntry = selling.entrySet()
                        .stream()
                        .min(Map.Entry.comparingByValue())
                        .orElse(null);

                Map<String, Object> maxCategory = new HashMap();
                maxCategory.put("categoryId", maxEntry.getKey().getId());
                maxCategory.put("categoryName", maxEntry.getKey().getName());
                maxCategory.put("quantity", maxEntry.getValue());
                Map<String, Object> minCategory = new HashMap();
                minCategory.put("categoryId", minEntry.getKey().getId());
                minCategory.put("categoryName", minEntry.getKey().getName());
                minCategory.put("quantity", minEntry.getValue());
                Map<String, Object> result = new HashMap<>();
                result.put("maxCategory", maxCategory);
                result.put("minCategory", minCategory);
                return result;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        return null;
    }

}