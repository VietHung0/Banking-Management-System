# 🗺️ ROADMAP — Banking-Management-System

> Mục đích: đánh dấu **đã làm gì** và **còn phải làm gì** mỗi khi mở máy lại.
> Project tự xây lại **từ đầu, giống hệt BankingPortal-API** (Spring Boot 3.3.1, Java 17).
> ✅ = xong · 🔄 = đang làm · ⬜ = chưa làm · *(cột phải = ước lượng % tổng dự án)*

---

## Trạng thái tổng thể

| Giai đoạn | Nội dung | Trạng thái | % tổng |
|---|---|---|---|
| **1** | Scaffold + config Spring Boot | ✅ xong, đã push Git | ~5% |
| **2** | **User + Đăng ký (Register)** | ✅ xong, đã test chạy thật | ~10% |
| **3** | Account + PIN (đổi lên trước — login cần accountNumber) | 🔄 đang làm | ~15% |
| **4** | Login + JWT + Security | ⬜ | ~12% |
| **5** | Gửi / rút tiền / chuyển khoản | ⬜ | ~15% |
| **6** | Transaction + sao kê email | ⬜ | ~10% |
| **7** | OTP + reset password | ⬜ | ~10% |
| **8** | Dashboard + cache (Redis/Caffeine) | ⬜ | ~10% |
| **9** | Hoàn thiện (CORS, Swagger, util, test) | ⬜ | ~15% |

> **Đã chốt (1):** JWT tách khỏi GĐ2. **Đã chốt (2):** đổi thứ tự — **GĐ3 = Account + PIN trước** (login bản gốc dùng `user.getAccount().getAccountNumber()`), **GĐ4 = Login + JWT**.

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

## ✅ Giai đoạn 2 — ĐÃ XONG (User + Đăng ký)

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

### Checklist task (đã hoàn thành lần lượt)
| # | Việc | File | Trạng thái |
|---|---|---|---|
| 1 | Entity User | `entity/User.java` | ✅ xong |
| 2 | Repository | `repository/UserRepository.java` | ✅ xong |
| 3 | Service (UserService + Impl + SecurityConfig bean PasswordEncoder) | `service/*`, `config/SecurityConfig` | ✅ xong |
| 4 | Exception + handler | `exception/*`, `GlobalExceptionHandler` | ✅ xong |
| 5 | Controller | `controller/UserController.java` | ✅ xong |
| 6 | Config Security tối thiểu + chạy thử | `config/WebSecurityConfig` | ✅ xong |

**Kết quả đã test thật:** chạy app trên `http://localhost:8180`, 3 test API đều đúng:
- Đăng ký hợp lệ → 200 "Đăng kí thành công"
- Trùng email → 400 "Email đã tồn tại" (qua `UserInvalidException` + handler)
- Email sai định dạng → 400 "must be a well-formed email address" (qua `MethodArgumentNotValidException` handler — đã thêm cho `@Valid`)

**Các file đã tạo ở GĐ2 (8 file):**
- `entity/User.java` — 7 cột, KHÔNG có `account` (thêm ở GĐ4), KHÔNG thêm `@Table` → Hibernate tạo bảng tên `user` (số ít, theo tên class).
- `repository/UserRepository.java` — `findByEmail`, `findByPhoneNumber`.
- `config/SecurityConfig.java` — bean `PasswordEncoder` (BCrypt).
- `service/UserService.java` + `service/UserServiceImpl.java` — `registerUser`: trùng email → throw `UserInvalidException`; `passwordEncoder.encode`; `save`.
- `exception/UserInvalidException.java` + `controller/GlobalExceptionHandler.java` — 3 handler: UserInvalidException→400, MethodArgumentNotValidException→400, Exception→500.
- `controller/UserController.java` — `POST /api/users/register` với `@Valid @RequestBody User`.
- `config/WebSecurityConfig.java` — cho phép `/api/users/register`, chặn mọi thứ khác (403), tắt form-login & http-basic → **API không có trang web; gọi bằng Postman/curl**.

**Mẹo từ test thực tế:** Vào trình duyệt gõ `http://localhost:8180/` bị 403 LÀ ĐÚNG — đây là API, không có trang web. Phải dùng Postman/curl.

**NOTE về dữ liệu:** DB chứa 2 user test (a@gmail.com, f@gmail.com) — khi commit để sạch có thể xóa (`DELETE FROM bankingapp.user;`).

---

## 🔄 Giai đoạn 3 — Account + PIN (đang làm)

**Vì sao làm trước:** login bản gốc dùng `user.getAccount().getAccountNumber()` → cần Account trước. Ngoài ra tạo Account ngay sau khi đăng ký là cách bản gốc làm (`saveUserWithAccount`).

Để tôi khảo sát bản gốc để lập checklist cụ thể cho GĐ3.

---

## ⬜ Giai đoạn 4 — Login + JWT (chưa bắt đầu)
`dto/UserLoginRequest` + `UserResponse`, `util/JwtUtil`, `security/JwtAuthenticationFilter` + `JwtAuthenticationEntryPoint`, `service/AuthService*` + `CustomUserDetailsService`, `controller/AuthController`, `WebSecurityConfig` stateless, `PasswordEncoder` bean.

---

## ⬜ Giai đoạn 5–9 (chưa bắt đầu)
Giai đoạn 5 → 9 lặp lại **đúng kiến trúc Giai đoạn 2**, chỉ đổi nghiệp vụ (giao dịch, transaction, OTP, dashboard, hoàn thiện) — càng về sau càng dễ.

---

## Hướng dẫn sử dụng

1. Mỗi lần mở máy: đọc file này để biết tiến độ.
2. Khi xong 1 mục → check ⬜ thành ✅ → commit.
3. Đối chiếu file gốc ở `D:\workspace\Project\BankingPortal-API\src\main\java\com\webapp\bankingportal\...` mỗi khi chưa rõ.