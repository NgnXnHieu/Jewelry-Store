package com.example.jewelrystore.Implement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jewelrystore.DTO.CheckoutDTO;
import com.example.jewelrystore.Entity.Address;
import com.example.jewelrystore.Entity.Cart;
import com.example.jewelrystore.Entity.Cart_Detail;
import com.example.jewelrystore.Entity.Checkout;
import com.example.jewelrystore.Entity.Checkout_Item;
import com.example.jewelrystore.Entity.Order;
import com.example.jewelrystore.Entity.Order_Detail;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Entity.Checkout.CheckoutStatus;
import com.example.jewelrystore.Entity.Checkout.ProductSource;
import com.example.jewelrystore.Entity.Order.PaymentMethod;
import com.example.jewelrystore.Form.Checkout.CheckoutCreateForm;
import com.example.jewelrystore.Form.Checkout.PlaceOrderForm;
import com.example.jewelrystore.Form.WebHook.SePayWebhookDTO;
import com.example.jewelrystore.Mapper.CheckoutMapper;
import com.example.jewelrystore.Repository.AddressRepository;
import com.example.jewelrystore.Repository.CartRepository;
import com.example.jewelrystore.Repository.Cart_DetailRepository;
import com.example.jewelrystore.Repository.CheckoutRepository;
import com.example.jewelrystore.Repository.OrderRepository;
import com.example.jewelrystore.Repository.ProductRepository;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.CheckoutService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CheckoutImpl implements CheckoutService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CheckoutRepository checkoutRepository;
    @Autowired
    Cart_DetailRepository cart_DetailRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CheckoutMapper checkoutMapper;

    @Override
    public Integer createCheckoutId(CheckoutCreateForm form, String username) {
        List<Checkout_Item> itemList = new ArrayList<>();
        Map<Integer, Long> list = form.getItemList();
        if (list == null) {
            throw new RuntimeException("No product to checkout");
        }
        Long totalQuantity = 0L;
        Double totalPrice = 0.0;
        Checkout checkout = new Checkout();
        // Kiểm tra xem lấy từ giỏ hay mua trực tiếp
        if (list.size() < 1) {
            throw new RuntimeException("No product to checkout");
        } else if (list.size() == 1) {
            Map.Entry<Integer, Long> entry = list.entrySet().iterator().next();
            Integer key = entry.getKey(); // Lấy Key
            Product product = productRepository.findById(key)
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));
            Cart cart = cartRepository.findByUserUsername(username)
                    .orElseThrow(() -> new RuntimeException("Cart Not Found"));
            Cart_Detail check = cart_DetailRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                    .orElse(null);
            if (check == null)
                checkout.setProducts_source(ProductSource.DIRECT_BUY);
        } else {
            checkout.setProducts_source(ProductSource.FROM_CART);
        }
        // Kiểm tra kho
        for (Map.Entry<Integer, Long> p : list.entrySet()) {
            Checkout_Item checkout_Item = new Checkout_Item();
            Product product = productRepository.findById(p.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Product not Found"));
            Long quantity = p.getValue();
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Not enough quantity");
            }
            checkout_Item.setQuantity(quantity);
            totalQuantity += quantity;
            checkout_Item.setProduct(product);
            Double price = product.getPrice() * quantity;
            checkout_Item.setTotalPrice(price);
            totalPrice += price;
            checkout_Item.setCheckout(checkout);
            itemList.add(checkout_Item);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        checkout.setCheckout_Items(itemList);
        // checkout.setPayment_method(null);
        checkout.setStatus(CheckoutStatus.PENDING);
        checkout.setUser(user);
        return checkoutRepository.save(checkout).getId();
    }

    @Override
    public CheckoutDTO getCheckout(Integer id, String username) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checkout Not Found"));
        if (checkout.getUser().getUsername().equalsIgnoreCase(username)) {
            return checkoutMapper.toCheckoutDTO(checkout);
        }
        return null;
    }

    @Override
    public String placeOrder(PlaceOrderForm form, String username) {
        // --- THÊM ĐOẠN NÀY ĐỂ DEBUG ---
        // System.out.println("DEBUG CHECK: CheckoutID = " + form.getCheckoutId());
        // System.out.println("DEBUG CHECK: AddressID = " + form.getAddressId());
        Checkout checkout = checkoutRepository.findById(form.getCheckoutId())
                .orElseThrow(() -> new EntityNotFoundException("This checkoutID not Valid"));
        if (!checkout.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not Permisson to use this checkoutID");
        }
        Address address = addressRepository.findById(form.getAddressId()).orElse(null);
        if (!address.getUser().getUsername()
                .equalsIgnoreCase(username)) {
            throw new AccessDeniedException("The address not exist in your account");
        }
        if ((checkout.getStatus() == CheckoutStatus.COMPLETED) || (checkout.getStatus() == CheckoutStatus.CANCELED)) {
            throw new RuntimeException("This order was placed or canceled");
        }
        if (form.getPayment_method() == PaymentMethod.COD) {
            resolveWithCOD(form, checkout, username);
            return "Place Successful";
        } else if (form.getPayment_method() == PaymentMethod.BANK) {
            resolveWithQR(form, checkout, username);
            return checkout.getQr();
        } else {
            return "Phương thức này chưa hỗ trợ";
        }
    }

    @Transactional
    public void resolveWithCOD(PlaceOrderForm form, Checkout checkout, String username) {
        Order order = checkout.getOrder();
        if (order == null)
            order = new Order();
        /*
         * Cách tối ưu khi trừ giỏ hàng
         * Ý tưởng:
         * - Lấy ra danh sách các cart_item có productId cần tìm(chỉ mất 1 kết nối tới
         * db)
         * - Chuyển danh sách về Map (truy cập nhanh hơn khi tìm sản phẩm có productId
         * trong Map)
         */
        // 1. Lấy ra các cart_detail có có productId trong list_checkout_items
        List<Integer> productIds = checkout.getCheckout_Items().stream()
                .map(item -> item.getProduct().getId()).collect(Collectors.toList());
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart Not Found"));
        List<Cart_Detail> listCart_Details = cart_DetailRepository.findByCartIdAndProductIdIn(cart.getId(),
                productIds);
        // 2.Chuyển về Map
        Map<Integer, Cart_Detail> cartMap = listCart_Details.stream()
                .collect(Collectors.toMap(cd -> cd.getProduct().getId(), cd -> cd));
        // Kiểm tra checkout này có đang ở trạng thái PAYMENT PENDING(trường hợp người
        // dùng đang thanh toán)
        // bằng qr nhưng bỏ dở -> tạo order với payment method là BANK)
        if (checkout.getStatus() == CheckoutStatus.PAYMENT_PENDING) {
            List<Checkout_Item> list = checkout.getCheckout_Items();
            List<Cart_Detail> listSavedCartDetails = new ArrayList<>();
            List<Cart_Detail> listDeletedCartDetails = new ArrayList<>();
            for (Checkout_Item item : list) {
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("The Quantity was changed");
                }
                Long quantity = item.getQuantity();
                // Xử lý trừ số lượng trong giỏ
                Cart_Detail cart_Detail = cartMap.get(item.getProduct().getId());
                if (cart_Detail != null) {
                    // Nếu số lượng trong giỏ lớn hơn -> trừ số lượng
                    if (cart_Detail.getQuantity() > item.getQuantity()) {
                        cart_Detail.setQuantity(cart_Detail.getQuantity() - quantity);
                        listSavedCartDetails.add(cart_Detail);
                    } else {
                        // Nếu số lượng trong giỏ nhỏ hơn hoặc bằng -> xóa luôn trong giỏ
                        cart_DetailRepository.delete(cart_Detail);
                        listDeletedCartDetails.add(cart_Detail);
                    }
                }
            }
            cart_DetailRepository.saveAll(listSavedCartDetails);
            cart_DetailRepository.deleteAll(listDeletedCartDetails);
        } else {
            Double totalPrice = 0.0;
            Long totalQuantity = 0L;
            List<Order_Detail> orderList = new ArrayList<>();
            List<Checkout_Item> list = checkout.getCheckout_Items();
            List<Cart_Detail> listSavedCartDetails = new ArrayList<>();
            List<Cart_Detail> listDeletedCartDetails = new ArrayList<>();
            List<Product> listProducts = new ArrayList<>();
            // Gán từng thông tin giá, số lượng cho checkout items
            for (Checkout_Item item : list) {
                Order_Detail order_Detail = new Order_Detail();
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("The Quantity was changed");
                }
                order_Detail.setOrder(order);
                order_Detail.setProduct(product);
                Double price = product.getPrice();
                Long quantity = item.getQuantity();
                order_Detail.setQuantity(quantity);
                order_Detail.setPrice(price);
                totalQuantity += quantity;
                totalPrice += price * quantity;
                product.setQuantity(product.getQuantity() - quantity);
                listProducts.add(product);
                // Xử lý trừ số lượng trong giỏ
                Cart_Detail cart_Detail = cartMap.get(item.getProduct().getId());
                if (cart_Detail != null) {
                    // Nếu số lượng trong giỏ lớn hơn -> trừ số lượng
                    if (cart_Detail.getQuantity() > item.getQuantity()) {
                        cart_Detail.setQuantity(cart_Detail.getQuantity() - quantity);
                        listSavedCartDetails.add(cart_Detail);
                    } else {
                        // Nếu số lượng trong giỏ nhỏ hơn hoặc bằng -> xóa luôn trong giỏ
                        listDeletedCartDetails.add(cart_Detail);
                    }

                }
                orderList.add(order_Detail);
            }
            order.setOrderDetails(orderList);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
            order.setUser(user);
            order.setQuantity(totalQuantity);
            order.setTotalAmount(totalPrice);
            productRepository.saveAll(listProducts);
            cart_DetailRepository.saveAll(listSavedCartDetails);
            cart_DetailRepository.deleteAll(listDeletedCartDetails);
        }
        // Set lại địa chỉ nếu đã có order/ set mới nết chưa có order
        Address address = addressRepository.findById(form.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address Not Found"));
        order.setPhone(address.getPhone());
        order.setAddress(address.getDetailAddress());
        order.setPayment_method(PaymentMethod.COD);
        order.setStatus("Đã xác nhận");
        checkout.setOrder(order);
        checkout.setStatus(CheckoutStatus.COMPLETED);
        orderRepository.save(order);
        checkoutRepository.save(checkout);
    }

    // Thêm transaction tránh rủi ro kho trừ số lượng nhưng trạng thái checkout chưa
    // kịp cập nhật -> lần sau quay lại thì lại trừ kho lần nữa -> lỗi
    @Transactional
    public void resolveWithQR(PlaceOrderForm form, Checkout checkout, String username) {
        // Kiểm tra checkout này đã có order chưa(trường hợp người dùng đang thanh toán
        // bằng qr nhưng bỏ dở -> tạo order với payment method là BANK)
        if (checkout.status == CheckoutStatus.PAYMENT_PENDING) {
            Order order = checkout.getOrder();
            LocalDateTime time = checkout.getQr_create_at();
            LocalDateTime exprireTime = time.plusMinutes(14);
            if (LocalDateTime.now().isAfter(exprireTime)) {
                String qr = createSePayQRCode(order);
                checkout.setQr_create_at(LocalDateTime.now());
                checkout.setQr(qr);
            }
            Address address = addressRepository.findById(form.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address Not Found"));
            checkout.setAddress(address);
            order.setAddress(address.getDetailAddress());
            order.setPhone(address.getPhone());
            orderRepository.save(order);
            checkoutRepository.save(checkout);
        } else {
            Order order = new Order();
            Double totalPrice = 0.0;
            Long totalQuantity = 0L;
            List<Order_Detail> orderList = new ArrayList<>();
            List<Checkout_Item> list = checkout.getCheckout_Items();
            List<Product> products = new ArrayList<>();
            // Gán từng thông tin giá, số lượng cho checkout items
            for (Checkout_Item item : list) {
                Order_Detail order_Detail = new Order_Detail();
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("The Quantity was changed");
                }
                order_Detail.setOrder(order);
                order_Detail.setProduct(product);
                Double price = product.getPrice();
                Long quantity = item.getQuantity();
                order_Detail.setQuantity(quantity);
                order_Detail.setPrice(price);
                totalQuantity += quantity;
                totalPrice += price * quantity;
                // Trừ số lượng trong kho
                product.setQuantity(product.getQuantity() - quantity);
                products.add(product);
                orderList.add(order_Detail);
            }
            order.setOrderDetails(orderList);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
            order.setUser(user);
            order.setQuantity(totalQuantity);
            order.setTotalAmount(totalPrice);
            Address address = addressRepository.findById(form.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address Not Found"));
            order.setPhone(address.getPhone());
            order.setAddress(address.getDetailAddress());
            order.setStatus("Chờ thanh toán");
            order.setPayment_method(PaymentMethod.BANK);
            orderRepository.save(order);
            String qr = createSePayQRCode(order);
            checkout.setQr_create_at(LocalDateTime.now());
            checkout.setQr(qr);
            checkout.setOrder(order);
            checkout.setStatus(CheckoutStatus.PAYMENT_PENDING);
            checkoutRepository.save(checkout);
            productRepository.saveAll(products);
        }
    }

    public String createSePayQRCode(Order order) {
        // 1. Cấu hình tài khoản SePay của bạn (Nên để trong application.properties)
        String bankId = "BIDV"; // Ví dụ: MB, VCB, TPBank...
        // Để tài khoản ảo
        String accountNo = "9624724H02"; // Số tài khoản của bạn
        String template = "compact"; // compact, qr_only, print
        // 2. Lấy dữ liệu đơn hàng
        long amount = order.getTotalAmount().longValue();

        // 3. Tạo nội dung chuyển khoản (QUAN TRỌNG)
        // Cú pháp nên là: [MãDựÁn] [ID] -> Ví dụ: "JEWELRY 105"
        // SePay sẽ dựa vào đây để bắn webhook
        String description = "JEWELRY " + order.getId();

        try {
            // Encode nội dung để tránh lỗi ký tự đặc biệt
            String encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString());

            // 4. Ghép chuỗi URL
            return String.format("https://qr.sepay.vn/img?bank=%s&acc=%s&template=%s&amount=%d&des=%s",
                    bankId, accountNo, template, amount, encodedDesc);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // Nếu muốn rollback theo chi tiết exception hơn thì sửa ở phần rollback
    // @Transactional(rollbackFor = Exception.class)
    // Khi thanh toán thành công
    @Transactional
    public void processPaymentSuccess(SePayWebhookDTO payload) {
        // 1. Lấy nội dung CK
        String content = payload.getContent(); // VD: "JEWELRY 105"
        Double amountPaid = payload.getTransferAmount();

        // 2. Tách lấy ID đơn hàng (Dùng Regex tìm số sau chữ JEWELRY)
        Integer orderId = extractIdFromContent(content);
        System.out.println(content);
        System.out.println(amountPaid);
        System.out.println(orderId);
        if (orderId != null) {
            Checkout checkout = checkoutRepository.findByOrderId(orderId).orElse(null);
            // 3. Kiểm tra đơn hàng & Số tiền
            if (checkout != null && (checkout.getStatus() == CheckoutStatus.PENDING
                    || checkout.getStatus() == CheckoutStatus.PAYMENT_PENDING)) {
                if (amountPaid.compareTo((checkout.getOrder().getTotalAmount())) >= 0) {
                    // Trừ số lượng trong giỏ hàng nếu giỏ hàng có sản phẩm đó
                    Cart cart = cartRepository.findByUserUsername(checkout.getUser().getUsername())
                            .orElseThrow(() -> new RuntimeException("Cart Not Found"));
                    List<Checkout_Item> list = checkout.getCheckout_Items();
                    List<Cart_Detail> listSave = new ArrayList<>();
                    List<Cart_Detail> listDelete = new ArrayList<>();
                    List<Integer> productIds = checkout.getCheckout_Items().stream()
                            .map(item -> item.getProduct().getId()).collect(Collectors.toList());
                    List<Cart_Detail> listCart_Details = cart_DetailRepository.findByCartIdAndProductIdIn(cart.getId(),
                            productIds);
                    // 2.Chuyển về Map
                    Map<Integer, Cart_Detail> cartMap = listCart_Details.stream()
                            .collect(Collectors.toMap(cd -> cd.getProduct().getId(), cd -> cd));
                    for (Checkout_Item item : list) {
                        Long quantity = item.getQuantity();
                        // Xử lý trừ số lượng trong giỏ
                        Cart_Detail cart_Detail = cartMap.get(item.getProduct().getId());
                        if (cart_Detail != null) {
                            // Nếu số lượng trong giỏ lớn hơn -> trừ số lượng
                            if (cart_Detail.getQuantity() > item.getQuantity()) {
                                cart_Detail.setQuantity(cart_Detail.getQuantity() - quantity);
                                listSave.add(cart_Detail);
                            } else {
                                // Nếu số lượng trong giỏ nhỏ hơn hoặc bằng -> xóa luôn trong giỏ
                                listDelete.add(cart_Detail);
                            }
                        }
                    }
                    // 4. UPDATE THÀNH CÔNG
                    checkout.setStatus(CheckoutStatus.COMPLETED);
                    // Xóa QR cũ đi
                    checkout.setQr(null);
                    checkoutRepository.save(checkout);
                    // Cập nhật giỏ hàng mới
                    cart_DetailRepository.saveAll(listSave);
                    cart_DetailRepository.deleteAll(listDelete);
                    System.out.println("Đã thanh toán xong đơn: " + orderId);
                } else {
                    throw new RuntimeException("Thanh toán thiếu tiền!!!");
                }
            }
        }
    }

    // Hàm phụ tách số
    private Integer extractIdFromContent(String content) {
        try {
            // Tìm chuỗi số nằm sau chữ JEWELRY
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("JEWELRY\\s*(\\d+)");
            java.util.regex.Matcher m = p.matcher(content);
            if (m.find())
                return Integer.parseInt(m.group(1));
        } catch (Exception e) {
        }
        return null;
    }

}
