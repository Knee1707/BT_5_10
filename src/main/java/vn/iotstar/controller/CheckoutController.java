package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.service.CartService;
import vn.iotstar.service.CurrentUserService;
import vn.iotstar.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public CheckoutController(CartService cartService, OrderService orderService,
                             CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String form(Model model, RedirectAttributes redirect) {
        User user = currentUserService.get();
        List<CartItem> items = cartService.getItems(user);
        if (items.isEmpty()) {
            redirect.addFlashAttribute("error", "Giỏ hàng đang trống.");
            return "redirect:/cart";
        }
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        model.addAttribute("user", user);
        return "checkout/form";
    }

    @PostMapping
    public String placeOrder(@RequestParam String recipientName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             @RequestParam(required = false) String note,
                             RedirectAttributes redirect) {
        try {
            Order order = orderService.checkoutCod(currentUserService.get(), recipientName, phone, address, note);
            redirect.addFlashAttribute("success", "Đặt hàng COD thành công! Mã đơn #" + order.getId());
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }
}
