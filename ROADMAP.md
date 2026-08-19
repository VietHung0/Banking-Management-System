# 🗺️ ROADMAP — Banking-Management-System

> Mục đích: đánh dấu **đã làm gì** và **còn phải làm gì** mỗi khi mở máy lại.
> Project tự xây lại **từ đầu, giống hệt BankingPortal-API** (Spring Boot 3.3.1, Java 17).
> ✅ = xong · 🔄 = đang làm · ⬜ = chưa làm · *(cột phải = ước lượng % tổng dự án)*

---

## Trạng thái tổng thể

| Giai đoạn | Nội dung | Trạng thái | % tổng |
|---|---|---|---|
| **1** | Scaffold + config Spring Boot | ✅ xong, đã push Git | ~5% |
| **2** | **User + Đăng ký (Register)** | 🔄 đang làm | ~10% |
| **3** | Login + JWT + Security | ⬜ | ~12% |
| **4** | Account + PIN | ⬜ | ~15% |
| **5** | Gửi / rút tiền / chuyển khoản | ⬜ | ~15% |
| **6** | Transaction + sao kê email | ⬜ | ~10% |
| **7** | OTP + reset password | ⬜ | ~10% |
| **8** | Dashboard + cache (Redis/Caffeine) | ⬜ | ~10% |
| **9** | Hoàn thiện (CORS, Swagger, util, test) | ⬜ | ~15% |

> **Đã chốt:** JWT được **tách khỏi** Giai đoạn 2 (GĐ2 chỉ register, GĐ3 riêng login + JWT) — để có mốc chạy được sớm và không cắn quá nhiều khái niệm mới một lúc.

---

## ✅ Giai đoạn 1 — ĐÃ XONG (config + scaffold)

**Mục tiêu:** Project Spring Boot chạy được, cấu hình nền tảng xong.

Đã tạo:
- `pom.xml` — parent `spring-boot-starter-parent` **3.3.1**, Java 17, 6 dep (web, data-jpa, validation, security, mysql, lombok)
- `BankingManagementSystemApplication.java` — entry point (`@SpringBootApplication`)
- `application.properties` — port 8180, DB `bankingapp`, `ddl-auto=update`
- `.gitignore`, Maven Wrapper (copy từ BankingPortal-API)
- 12 package thư mục sẵn (config, controller, dto, entity, exception, mapper, repository, security, service, type, util)

**Kiểm chứng:** `./mvnw compile` → OK.
**Còn thiếu:** chưa chạy thật (cần MySQL + DB `bankingapp`), chưa tạo file test mặc định.

---

## 🔄 Giai đoạn 2 — ĐANG LÀM (User + Đăng ký)

### Bản đồ kiến trúc (request đi qua đâu)
```
[1] HTTP Request (JSON từ Postman)
   ↓
[2] Controller ─ tiếp nhận, gọi service          ← file: controller/*.java
   ↓  (dữ liệu dạng DTO)
[3] Service ─ "bộ não": kiểm tra trùng, mã hóa   ← file: service/*ServiceImpl.java
   ↓  (dùng interface)
[4] Repository ─ cầu nối DB, tự sinh SQL         ← file: repository/*.java
   ↓
   Database (bảng `users`)
```
> Mẹo: **Controller → Service → Repository → Entity** — của dữ liệu đi vào; từ Entity nếu đọc DB ra.

### Checklist task (làm lần lượt)
| # | Việc | File | Trạng thái |
|---|---|---|---|
| 1 | Entity User | `entity/User.java` | ✅ xong |
| 2 | Repository | `repository/UserRepository.java` | ✅ xong |
| 3 | Service (UserService + Impl + SecurityConfig bean PasswordEncoder) | `service/*`, `config/SecurityConfig` | ✅ xong |
| 4 | Exception + handler | `exception/*`, `GlobalExceptionHandler` | 🔄 đang làm |
| 5 | Controller | `controller/UserController.java` | ⬜ |
| 6 | Config Security tối thiểu + chạy thử Postman | `config/WebSecurityConfig` | ⬜ |

### Điểm cần lưu ý khi làm
- `User.java` đã viết **KHÔNG có field `Account`** (quan hệ `@OneToOne` sẽ thêm ở Giai đoạn 4) và bỏ 2 import thừa so với bản gốc.
- Đã chốt: **KHÔNG tạo DTO đăng ký** — bản gốc bỏ luôn `User` vào `@RequestBody` (controller sẽ dùng `@RequestBody User user`).
- Task 3 đã làm: `registerUser` = kiểm tra trùng email (`findByEmail`.isPresent()) → `passwordEncoder.encode` → `save` → trả về chuỗi. Đơn giản hơn gốc (chưa có ValidationUtil/UserResponse/JsonUtil).
- Chưa có JWT: `WebSecurityConfig` chỉ cần cho phép `/api/users/register`.

### Tham chiếu bản gốc
- `BankingPortal-API/src/main/java/com/webapp/bankingportal/entity/User.java` (đã đọc)
- `.../repository/UserRepository.java` (đã đọc; 3 method: `findByEmail`, `findByPhoneNumber`, `findByAccountAccountNumber`)

---

## ⬜ Giai đoạn 3 — Login + JWT (chưa bắt đầu)
`dto/UserLoginRequest` + `UserResponse`, `util/JwtUtil`, `security/JwtAuthenticationFilter` + `JwtAuthenticationEntryPoint`, `service/AuthService*` + `CustomUserDetailsService`, `controller/AuthController`, `WebSecurityConfig` stateless, `PasswordEncoder` bean.

---

## ⬜ Giai đoạn 4–9 (chưa bắt đầu)
Giai đoạn 4 → 8 lặp lại **đúng kiến trúc Giai đoạn 2**, chỉ đổi nghiệp vụ (Account/PIN, giao dịch, transaction, OTP, dashboard) — nên càng về sau càng dễ.

---

## Hướng dẫn sử dụng

1. Mỗi lần mở máy: đọc file này để biết tiến độ.
2. Khi xong 1 mục → check ⬜ thành ✅ → commit.
3. Đối chiếu file gốc ở `D:\workspace\Project\BankingPortal-API\src\main\java\com\webapp\bankingportal\...` mỗi khi chưa rõ.