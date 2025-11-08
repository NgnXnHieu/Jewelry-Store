package com.example.jewelrystore.Implement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jewelrystore.DTO.ProductDTO;
import com.example.jewelrystore.DTO.UserDTO;
import com.example.jewelrystore.Entity.Inventory_History;
import com.example.jewelrystore.Entity.Product;
import com.example.jewelrystore.Entity.User;
import com.example.jewelrystore.Form.ProductForm.ProductUpdateForm;
import com.example.jewelrystore.Form.UserForm.RegisterForm;
import com.example.jewelrystore.Form.UserForm.UserCreateForm;
import com.example.jewelrystore.Form.UserForm.UserUpdateForm;
import com.example.jewelrystore.Mapper.UserMapper;
import com.example.jewelrystore.Repository.UserRepository;
import com.example.jewelrystore.Service.UserService;

import net.coobird.thumbnailator.Thumbnails;

@Service

public class UserImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDTO register(RegisterForm form) {
        User created = userRepository.findByUsername(form.getUsername()).orElse(null);
        if (created == null) {
            created = userMapper.toEntity(form);
            created.setRole("USER");
            created.setPassword(passwordEncoder.encode(form.getPassword()));
            userRepository.save(created);
            return userMapper.toUserDTO(created);
        }
        return null;
    }

    @Override
    public UserDTO updateUser(Integer id, UserUpdateForm userUpdateForm, MultipartFile image) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            String imagePath = null;
            String image_url = userUpdateForm.getImage_url();

            try {
                if (image != null && !image.isEmpty()) {
                    String uploadDir = "D:\\DACN\\Picture";
                    Files.createDirectories(Paths.get(uploadDir));

                    // Tên file: thời gian + tên gốc
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // ✅ Resize ảnh về kích thước 500x500 và lưu xuống
                    Thumbnails.of(image.getInputStream())
                            .size(700, 700)
                            .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                            .outputQuality(0.85) // giảm dung lượng, 85% chất lượng
                            .toFile(filePath.toFile());

                    imagePath = fileName;

                } else if (image_url != null && !image_url.isEmpty()) {
                    imagePath = image_url; // dùng link nếu có
                }

                userMapper.updateUser(userUpdateForm, existing);
                // 🔹 Update entity
                if (imagePath != null) {
                    existing.setImage_url(imagePath);
                }
                userRepository.save(existing);
                return userMapper.toUserDTO(existing);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
            }

        }
        return null;

    }

    @Override
    public Page<UserDTO> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserDTO);
    }

    @Override
    public UserDTO getUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::toUserDTO).orElse(null);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getInfor(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO create(UserCreateForm createForm, MultipartFile image) {
        User user = userRepository.findByUsername(createForm.getUsername()).orElse(null);
        if (user == null) {
            String imagePath = null;
            String image_url = createForm.getImage_url();

            try {
                if (image != null && !image.isEmpty()) {
                    String uploadDir = "D:\\DACN\\Picture";
                    Files.createDirectories(Paths.get(uploadDir));

                    // Tên file: thời gian + tên gốc
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // ✅ Resize ảnh về kích thước 500x500 và lưu xuống
                    Thumbnails.of(image.getInputStream())
                            .size(700, 700)
                            .keepAspectRatio(true) // giữ tỉ lệ gốc, không méo hình
                            .outputQuality(0.85) // giảm dung lượng, 85% chất lượng
                            .toFile(filePath.toFile());

                    imagePath = fileName;

                } else if (image_url != null && !image_url.isEmpty()) {
                    imagePath = image_url; // dùng link nếu có
                }

                // 🔹 Tạo entity và lưu DB
                user = userMapper.toStaffEntity(createForm);
                user.setImage_url(imagePath);
                user.setPassword(passwordEncoder.encode(createForm.getPassword()));
                userRepository.save(user);
                return userMapper.toUserDTO(user);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu ảnh sản phẩm", e);
            }
        }
        return null;

    }

    @Override
    public Page<UserDTO> getAllHumanresources(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findByRoleNot("CUSTOMER", sortedPageable).map(userMapper::toUserDTO);

    }
}
