package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderStatus;
import vn.iotstar.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, OrderStatus status);

    Optional<Order> findByIdAndUser(Long id, User user);
}
