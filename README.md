# MINI SHOP - Giỏ hàng, Thanh toán COD, Lịch sử đơn hàng (vai trò User)

Bài tập BT_5_10 môn **Lập trình web** - HCMUTE. Thực hiện 3 chức năng cho vai trò **User**:

1. **Giỏ hàng:** thêm, xóa, sửa (thay đổi số lượng) trong giới hạn tồn kho.
2. **Thanh toán đơn hàng bằng COD.**
3. **Lịch sử đặt hàng, lọc theo trạng thái:** Đơn hàng mới, Đã xác nhận, Chuẩn bị hàng, Vận chuyển, Giao hàng, Đã giao, Đơn hàng hủy, Đơn hàng hoàn.

## Sinh viên thực hiện
- **Họ tên:** Nguyễn Huỳnh Dĩ Khang
- **MSSV:** 24133027

## Công nghệ
Spring Boot 4.1.1 · Spring Security 7 · Thymeleaf · Spring Data JPA · SQL Server (mặc định) / H2 (chạy thử) · BCrypt · Maven.

## Chức năng chi tiết
- **Giỏ hàng** (`/cart`): thêm từ trang sản phẩm (`/cart/add`), cập nhật số lượng (`/cart/update`, tự giới hạn 1..tồn kho),
  xóa (`/cart/remove`), hiển thị thành tiền từng dòng và tổng cộng.
- **Thanh toán COD** (`/checkout`): nhập thông tin giao hàng, phương thức COD → tạo đơn trạng thái "Đơn hàng mới",
  trừ tồn kho, xóa giỏ hàng.
- **Lịch sử đơn hàng** (`/orders`): lọc theo từng trạng thái qua chip, xem chi tiết (`/orders/{id}`),
  hủy đơn khi còn ở trạng thái Mới/Đã xác nhận/Chuẩn bị hàng. 8 trạng thái định nghĩa trong `OrderStatus`.
  (Admin/DB đổi trạng thái đơn để User quan sát — dữ liệu mẫu đã tạo sẵn đơn ở cả 8 trạng thái.)

## Tài khoản mẫu (seed khi khởi động)
- User: `user01` / `123456` (đã có 8 sản phẩm và 8 đơn hàng ở 8 trạng thái khác nhau để demo lọc).

## Cách chạy

### H2 (chạy thử nhanh, không cần cài DB)
```bash
mvn -DskipTests clean package
java -jar target/shop-cart-order-1.0.0.jar --spring.profiles.active=h2
```
Mở http://localhost:8080 → đăng nhập `user01` / `123456`.

### SQL Server (mặc định)
Sửa `spring.datasource.*` trong `application.properties` cho khớp máy, rồi `mvn spring-boot:run`.

> Lưu ý: project dùng JDK 26; nếu máy có nhiều JDK, trỏ `JAVA_HOME` về JDK 26 trước khi build.
