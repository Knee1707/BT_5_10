package vn.iotstar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.*;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roles, UserRepository users,
                               ProductRepository products, OrderRepository orders,
                               PasswordEncoder encoder) {
        return args -> {
            Role userRole = roles.findByName("ROLE_USER").orElseGet(() -> roles.save(new Role("ROLE_USER")));
            roles.findByName("ROLE_ADMIN").orElseGet(() -> roles.save(new Role("ROLE_ADMIN")));

            if (users.existsByUsername("user01")) {
                return;
            }

            User user = new User();
            user.setUsername("user01");
            user.setEmail("user01@gmail.com");
            user.setFullName("Nguyễn Văn User");
            user.setPhone("0901234567");
            user.setAddress("123 Lê Lợi, Quận 1, TP.HCM");
            user.setPassword(encoder.encode("123456"));
            user.setRole(userRole);
            User savedUser = users.save(user);

            String[] names = {"Áo thun nam", "Quần jean", "Giày sneaker", "Balo laptop",
                    "Tai nghe bluetooth", "Đồng hồ đeo tay", "Bình giữ nhiệt", "Sạc dự phòng"};
            List<Product> productList = new java.util.ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                Product p = new Product();
                p.setName(names[i]);
                p.setDescription("Mô tả cho " + names[i]);
                p.setPrice(BigDecimal.valueOf(150000L + i * 50000L));
                p.setStock(20 + i);
                productList.add(products.save(p));
            }

            OrderStatus[] statuses = OrderStatus.values();
            for (int i = 0; i < statuses.length; i++) {
                Product p = productList.get(i);
                Order order = new Order();
                order.setUser(savedUser);
                order.setStatus(statuses[i]);
                order.setPaymentMethod("COD");
                order.setRecipientName(savedUser.getFullName());
                order.setPhone(savedUser.getPhone());
                order.setAddress(savedUser.getAddress());
                order.setCreatedAt(LocalDateTime.now().minusDays(i));

                OrderItem item = new OrderItem();
                item.setProduct(p);
                item.setProductName(p.getName());
                item.setPrice(p.getPrice());
                item.setQuantity(i + 1);
                order.addItem(item);
                order.setTotalAmount(item.getLineTotal());
                orders.save(order);
            }
        };
    }
}
