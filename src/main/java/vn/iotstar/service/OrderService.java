package vn.iotstar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.*;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@Service
public class OrderService {

    private static final EnumSet<OrderStatus> CANCELLABLE =
            EnumSet.of(OrderStatus.NEW, OrderStatus.CONFIRMED, OrderStatus.PREPARING);

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order checkoutCod(User user, String recipientName, String phone, String address, String note) {
        List<CartItem> cartItems = cartItemRepository.findByUserOrderByIdAsc(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setPaymentMethod("COD");
        order.setRecipientName(recipientName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = Math.min(cartItem.getQuantity(), product.getStock());
            if (quantity <= 0) continue;

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            order.addItem(orderItem);

            total = total.add(orderItem.getLineTotal());
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        cartItemRepository.deleteByUser(user);
        return order;
    }

    public List<Order> history(User user, OrderStatus status) {
        if (status == null) {
            return orderRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return orderRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
    }

    public Order getOwned(User user, Long orderId) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
    }

    @Transactional
    public void cancel(User user, Long orderId) {
        Order order = getOwned(user, orderId);
        if (!CANCELLABLE.contains(order.getStatus())) {
            throw new IllegalArgumentException("Không thể hủy đơn ở trạng thái: " + order.getStatus().getLabel());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
