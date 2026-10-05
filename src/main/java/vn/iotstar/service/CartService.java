package vn.iotstar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getItems(User user) {
        return cartItemRepository.findByUserOrderByIdAsc(user);
    }

    public long count(User user) {
        return cartItemRepository.countByUser(user);
    }

    public BigDecimal total(List<CartItem> items) {
        return items.stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("Sản phẩm đã hết hàng");
        }
        CartItem item = cartItemRepository.findByUserAndProduct(user, product).orElse(null);
        int current = item == null ? 0 : item.getQuantity();
        int target = clamp(current + quantity, 1, product.getStock());
        if (item == null) {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
        }
        item.setQuantity(target);
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(User user, Long cartItemId, int quantity) {
        CartItem item = getOwnedItem(user, cartItemId);
        item.setQuantity(clamp(quantity, 1, item.getProduct().getStock()));
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(User user, Long cartItemId) {
        cartItemRepository.delete(getOwnedItem(user, cartItemId));
    }

    @Transactional
    public void clear(User user) {
        cartItemRepository.deleteByUser(user);
    }

    private CartItem getOwnedItem(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Mục giỏ hàng không tồn tại"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Không có quyền thao tác mục giỏ hàng này");
        }
        return item;
    }

    private int clamp(int value, int min, int max) {
        if (value < min) return min;
        return Math.min(value, max);
    }
}
