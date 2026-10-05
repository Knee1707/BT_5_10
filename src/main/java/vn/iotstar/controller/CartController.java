package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.User;
import vn.iotstar.service.CartService;
import vn.iotstar.service.CurrentUserService;

import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CartController(CartService cartService, CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/cart")
    public String view(Model model) {
        User user = currentUserService.get();
        List<CartItem> items = cartService.getItems(user);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        return "cart/view";
    }

    @PostMapping("/cart/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes redirect) {
        try {
            cartService.addToCart(currentUserService.get(), productId, quantity);
            redirect.addFlashAttribute("success", "Đã thêm vào giỏ hàng.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Long cartItemId,
                         @RequestParam int quantity,
                         RedirectAttributes redirect) {
        try {
            cartService.updateQuantity(currentUserService.get(), cartItemId, quantity);
            redirect.addFlashAttribute("success", "Đã cập nhật số lượng.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long cartItemId, RedirectAttributes redirect) {
        cartService.removeItem(currentUserService.get(), cartItemId);
        redirect.addFlashAttribute("success", "Đã xóa khỏi giỏ hàng.");
        return "redirect:/cart";
    }
}
