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
| **3** | Account + PIN (đổi lên trước — login cần accountNumber) | ✅ xong, đã test chạy thật | ~15% |
| **4** | Login + JWT + Security | ✅ xong, đã test chạy thật | ~12% |
| **5** | Gửi / rút tiền / chuyển khoản | ✅ xong, đã test chạy thật | ~15% |
| **6** | Transaction history + message chuyển tiền | ✅ xong core, bỏ qua email tạm thời | ~10% |
| **7** | OTP + reset password | ⬜ backlog phụ | ~10% |
| **8** | Dashboard + cache (Redis/Caffeine) | ✅ dashboard xong, cache backlog phụ | ~10% |
| **9** | Hoàn thiện (CORS, Swagger, util, test) | 🔄 core đã xong, còn polish tài liệu/test | ~15% |

> **Đã chốt (1):** JWT tách khỏi GĐ2. **Đã chốt (2):** đổi thứ tự — **GĐ3 = Account + PIN trước** (login bản gốc dùng `user.getAccount().getAccountNumber()`), **GĐ4 = Login + JWT**.

### Mốc hiện tại — Core API + UI đã hoàn thiện

**Đã xong mốc lớn:** backend có đủ 11 API chính và frontend đã nối được toàn bộ flow chính:

- Register
- Login
- Dashboard user
- Dashboard account
- Check PIN
- Create PIN
- Update PIN
- Deposit
- Withdraw
- Fund Transfer
- Transaction History

**UI đã hoàn thiện ở mức demo/phỏng vấn:**

- Login/Register theo phong cách web banking.
- Main layout sau login có sidebar cố định.
- Dashboard, Profile, Account Info, PIN, Deposit, Withdraw, Fund Transfer, Transaction History đã có màn riêng.
- Format domain theo hướng ngân hàng Nhật: JPY, account number 7 chữ số, bank code `0038`, branch/branch code, account status.
- Fund Transfer đã có tra tên người nhận theo account number.
- Fund Transfer đã có lời nhắn chuyển tiền và lưu vào transaction history.

**Kết luận hiện tại:** Project đã đủ để demo chức năng core end-to-end. Phần còn lại chủ yếu là tính năng phụ/hạ tầng để giống API gốc hơn.

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

## ✅ Giai đoạn 3 — ĐÃ XONG (Account + PIN)

**Vì sao làm trước:** login bản gốc dùng `user.getAccount().getAccountNumber()` → cần Account trước. Ngoài ra tạo Account ngay sau khi đăng ký là cách bản gốc làm (`saveUserWithAccount`).

### Đã làm
- `entity/Account.java` — tạo Account entity, quan hệ `User` ↔ `Account`, đổi field `Pin` thành `pin`, thêm default `accountType = "Savings"`, `branch = "NIT"`, `ifscCode = "NIT001"`.
- `entity/User.java` — thêm quan hệ `@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)` tới Account.
- `repository/AccountRepository.java` — thêm `findByAccountNumber`.
- `exception/AccountNotFoundException.java`, `exception/InvalidPinException.java` — thêm exception cho Account/PIN.
- `controller/GlobalExceptionHandler.java` — thêm handler cho Account/PIN exception.
- `service/AccountService.java`, `service/AccountServiceImpl.java` — tạo account, check PIN, create PIN, update PIN; password/PIN đều kiểm tra bằng `PasswordEncoder.matches`, PIN lưu BCrypt.
- `service/UserServiceImpl.java` — register user xong tự tạo account và gắn account vào user.
- `dto/PinRequest.java`, `dto/PinUpdateRequest.java` — DTO cho create/update PIN.
- `controller/AccountController.java` — 3 API PIN:
  - `GET /api/account/pin/check?accountNumber=...`
  - `POST /api/account/pin/create`
  - `POST /api/account/pin/update`
- `config/WebSecurityConfig.java` — mở tạm `/api/account/pin/**` để test trước khi làm JWT.
- `application.properties` — thêm `allowPublicKeyRetrieval=true` vào MySQL URL để app connect được MySQL local.

### Kết quả đã test thật bằng curl
- Chạy app bằng `mvnw.cmd clean spring-boot:run` → OK trên `http://localhost:8180`.
- Register user mới → 200 `"Đăng kí thành công"`.
- DB sinh account mới, account test nhận được: `766cc7`.
- `GET /api/account/pin/check?accountNumber=766cc7` trước khi tạo PIN → 200 `"PIN chưa được tạo"`.
- `POST /api/account/pin/create` với password đúng và PIN `1234` → 200 `"Tạo PIN thành công"`.
- Check PIN lại → 200 `"PIN đã được tạo"`.
- `POST /api/account/pin/update` đổi `1234` → `5678` → 200 `"Đổi PIN thành công"`.
- Tạo PIN lần 2 → 400 `"PIN đã tồn tại"`.
- Update với old PIN sai → 400 `"PIN không đúng"`.

**NOTE về dữ liệu test:** DB có user test `testpin_20260827_0338@gmail.com` và account `766cc7`.

---

## ✅ Giai đoạn 4 — ĐÃ XONG (Login + JWT + Security)

**Mục tiêu:** User login bằng `email` hoặc `accountNumber` + password, server trả JWT token. Sau đó các API cần bảo vệ sẽ yêu cầu header `Authorization: Bearer <token>`.

### Cách chia nhỏ để học
Nên chia GĐ4 thành 2 nửa:
- **Nửa 1:** Login trả token. ✅ đã xong, đã test bằng curl.
- **Nửa 2:** Dùng token để bảo vệ API. ✅ đã xong, đã test bằng curl.

### Checklist task
| # | Việc | File dự kiến | Trạng thái |
|---|---|---|---|
| 1 | Thêm dependency JWT | `pom.xml` | ✅ |
| 2 | Tạo DTO login | `dto/LoginRequest.java` | ✅ |
| 3 | Thêm query tìm user theo account number | `repository/UserRepository.java` | ✅ |
| 4 | Tạo JWT helper | `util/JwtUtil.java` | ✅ |
| 5 | Tạo AuthService interface | `service/AuthService.java` | ✅ |
| 6 | Tạo AuthServiceImpl, xử lý login | `service/AuthServiceImpl.java` | ✅ |
| 7 | Tạo AuthController | `controller/AuthController.java` | ✅ |
| 8 | Test login trả JWT token | curl/Postman | ✅ |
| 9 | Tạo TokenService theo style project gốc | `service/TokenService.java`, `service/TokenServiceImpl.java` | ✅ |
| 10 | Tạo JwtAuthenticationFilter | `security/JwtAuthenticationFilter.java` | ✅ |
| 11 | Sửa WebSecurityConfig sang JWT/stateless | `config/WebSecurityConfig.java` | ✅ |
| 12 | Khóa lại `/api/account/**`, chỉ mở register/login | `config/WebSecurityConfig.java` | ✅ |
| 13 | Test API không token bị chặn, có token thì chạy | curl | ✅ |

### Luồng login cần hiểu
```
Client gửi identifier + password
   ↓
AuthController nhận request
   ↓
AuthService tìm user bằng email hoặc accountNumber
   ↓
PasswordEncoder.matches(rawPassword, encodedPassword)
   ↓
Lấy accountNumber từ user.getAccount().getAccountNumber()
   ↓
JwtUtil.generateToken(accountNumber)
   ↓
Trả token về client
```

### Luồng request có JWT
```
Client gửi Authorization: Bearer <token>
   ↓
JwtAuthenticationFilter đọc token
   ↓
JwtUtil extract accountNumber
   ↓
TokenService load user theo accountNumber
   ↓
Set Authentication vào SecurityContext
   ↓
Controller được chạy nếu token hợp lệ
```

### Đã test ở GĐ4
- `POST /api/auth/login` bằng email `testpin_20260827_0338@gmail.com` + password `123456` → 200, trả JWT.
- `POST /api/auth/login` bằng accountNumber `766cc7` + password `123456` → 200, trả JWT.
- `JwtUtil` đã sửa sang `Keys.hmacShaKeyFor(...)`, `signWith(getSigningKey(), SignatureAlgorithm.HS256)`, `parserBuilder()`.
- Spring log nhận `tokenServiceImpl` làm `UserDetailsService`.
- `GET /api/account/pin/check?accountNumber=766cc7` không token → 403.
- `GET /api/account/pin/check?accountNumber=766cc7` có `Authorization: Bearer <jwt>` → 200 `"PIN đã được tạo"`.
- `config/WebSecurityConfig.java` đã chuyển sang stateless JWT và chỉ permit:
  - `/api/users/register`
  - `/api/auth/login`

**NOTE:** Project mới đi theo style project gốc: không dùng `CustomUserDetailsService`; thay vào đó `TokenService extends UserDetailsService`, `TokenServiceImpl` implement `loadUserByUsername(accountNumber)`.

---

## ✅ Giai đoạn 5 — ĐÃ XONG (Gửi tiền / rút tiền / chuyển khoản)

**Mục tiêu:** Account có thể thay đổi `balance` qua 3 nghiệp vụ chính:
- Deposit: nạp/gửi tiền vào tài khoản.
- Withdraw: rút tiền khỏi tài khoản.
- Transfer: chuyển tiền từ tài khoản này sang tài khoản khác.

### Checklist task
| # | Việc | File dự kiến | Trạng thái |
|---|---|---|---|
| 1 | Đối chiếu project gốc để xem DTO/endpoint/logic tiền | `BankingPortal-API` | ✅ |
| 2 | Tạo DTO gửi/rút tiền | `dto/AmountRequest.java` | ✅ |
| 3 | Tạo DTO chuyển khoản | `dto/FundTransferRequest.java` | ✅ |
| 4 | Tạo util lấy account đang login từ JWT | `util/LoggedinUser.java` | ✅ |
| 5 | Thêm method service cho 3 nghiệp vụ | `service/AccountService.java` | ✅ |
| 6 | Viết logic deposit | `service/AccountServiceImpl.java` | ✅ |
| 7 | Viết logic withdraw: check account, check PIN, check đủ tiền | `service/AccountServiceImpl.java` | ✅ |
| 8 | Viết logic transfer: check sender/receiver, check PIN, check đủ tiền, trừ/cộng balance | `service/AccountServiceImpl.java` | ✅ |
| 9 | Thêm API deposit/withdraw/transfer | `controller/AccountController.java` | ✅ |
| 10 | Test không có JWT bị chặn | curl | ✅ |
| 11 | Test có JWT: deposit đúng, withdraw đúng, transfer đúng | curl | ✅ |
| 12 | Test case lỗi: sai PIN, không đủ tiền, account không tồn tại | curl | ✅ |
| 13 | Note kết quả test vào roadmap | `ROADMAP.md` | ✅ |

### Luồng deposit
```
Client gửi JWT + accountNumber + amount
   ↓
AccountController
   ↓
AccountServiceImpl tìm account
   ↓
Cộng amount vào balance
   ↓
Save account
```

### Luồng withdraw
```
Client gửi JWT + accountNumber + pin + amount
   ↓
AccountController
   ↓
AccountServiceImpl tìm account
   ↓
Check PIN bằng PasswordEncoder.matches
   ↓
Check balance đủ tiền
   ↓
Trừ amount khỏi balance
   ↓
Save account
```

### Luồng transfer
```
Client gửi JWT + fromAccountNumber + toAccountNumber + pin + amount
   ↓
AccountController
   ↓
AccountServiceImpl tìm account gửi và account nhận
   ↓
Check PIN của account gửi
   ↓
Check account gửi đủ tiền
   ↓
Trừ balance account gửi, cộng balance account nhận
   ↓
Save cả 2 account
```

**NOTE:** GĐ5 vẫn dùng JWT Security từ GĐ4. Các API `/api/account/**` hiện đã bị khóa, nên khi test phải gửi header `Authorization: Bearer <jwt>`.

### Kết quả đã test ở GĐ5
- Register thêm user nhận tiền `transfer_target_20260828@gmail.com` → account nhận `dd3d2f`.
- Login user nguồn `testpin_20260827_0338@gmail.com` → lấy JWT cho account `766cc7`.
- `POST /api/account/deposit` không JWT → 403.
- `POST /api/account/deposit` có JWT, PIN `5678`, amount `1000000` → 200 `"Gửi tiền thành công"`.
- `POST /api/account/withdraw` có JWT, PIN `5678`, amount `200000` → 200 `"Rút tiền thành công"`.
- `POST /api/account/fund-transfer` có JWT, chuyển `300000` từ `766cc7` sang `dd3d2f` → 200 `"Chuyển tiền thành công"`.
- Sai PIN → 400 `"PIN không đúng"`.
- Account nhận không tồn tại → 404 `"Không tìm thấy tài khoản nhận"`.
- Rút quá số dư → 400 `"Số dư không đủ"`.
- Giao dịch vượt `10000000` → 400 `"Số tiền không được vượt quá 10000000"`.
- Balance sau test: account `766cc7` còn `500000`, account `dd3d2f` có `300000`.

---

## ✅ Giai đoạn 6 — Transaction history + message chuyển tiền

**Mục tiêu:** Mỗi lần deposit/withdraw/transfer thành công thì hệ thống phải lưu lại lịch sử giao dịch. User có thể xem lịch sử giao dịch trên frontend. Riêng sao kê email tạm thời bỏ qua, để backlog phụ.

### Checklist task
| # | Việc | File dự kiến | Trạng thái |
|---|---|---|---|
| 1 | Đối chiếu project gốc để xem Transaction entity/service/controller | `BankingPortal-API` | ✅ |
| 2 | Tạo enum loại giao dịch | `entity/TransactionType.java` | ✅ |
| 3 | Tạo Transaction entity | `entity/Transaction.java` | ✅ |
| 4 | Tạo Transaction DTO response | `dto/TransactionDTO.java` | ✅ |
| 5 | Tạo TransactionRepository | `repository/TransactionRepository.java` | ✅ |
| 6 | Tạo TransactionService interface | `service/TransactionService.java` | ✅ |
| 7 | Tạo TransactionServiceImpl skeleton/import | `service/TransactionServiceImpl.java` | ✅ |
| 8 | Viết logic lấy transaction theo accountNumber | `service/TransactionServiceImpl.java` | ✅ |
| 9 | Inject TransactionRepository vào AccountServiceImpl | `service/AccountServiceImpl.java` | ✅ |
| 10 | Sau deposit thành công thì save Transaction | `service/AccountServiceImpl.java` | ✅ |
| 11 | Sau withdraw thành công thì save Transaction | `service/AccountServiceImpl.java` | ✅ |
| 12 | Sau transfer thành công thì save Transaction | `service/AccountServiceImpl.java` | ✅ |
| 13 | Thêm API xem lịch sử giao dịch | `controller/AccountController.java` | ✅ |
| 14 | Test deposit/withdraw/transfer sinh transaction đúng | curl + DB | ✅ |
| 15 | Test API xem transaction history bằng JWT | curl | ✅ |
| 16 | Thêm API tra tên người nhận khi nhập account nhận | `controller/AccountController.java`, `service/AccountServiceImpl.java`, `dto/RecipientResponse.java` | ✅ |
| 17 | Thêm lời nhắn chuyển tiền | `dto/FundTransferRequest.java`, `entity/Transaction.java` | ✅ |
| 18 | Hiển thị lời nhắn trong transaction history | `TransactionDTO`, Angular transaction model/history UI | ✅ |
| 19 | Thêm dependency/config gửi email nếu làm sao kê | `pom.xml`, `application.properties` | ⬜ backlog |
| 20 | Viết logic gửi sao kê email | `service/TransactionServiceImpl.java` | ⬜ backlog |
| 21 | Thêm API gửi sao kê email | `controller/AccountController.java` | ⬜ backlog |
| 22 | Test gửi sao kê email | curl/email inbox | ⬜ backlog |

### Luồng lưu transaction
```
Client gọi deposit/withdraw/transfer có JWT
   ↓
AccountController lấy accountNumber từ LoggedinUser
   ↓
AccountServiceImpl xử lý balance
   ↓
Nếu nghiệp vụ thành công thì tạo Transaction
   ↓
TransactionRepository.save(transaction)
```

### Luồng xem lịch sử giao dịch
```
Client gọi GET /api/account/transactions có JWT
   ↓
AccountController lấy accountNumber từ LoggedinUser
   ↓
TransactionServiceImpl tìm transaction liên quan accountNumber
   ↓
Trả danh sách TransactionDTO
```

### Luồng tra người nhận khi chuyển khoản
```
User nhập account nhận ở Fund Transfer
   ↓
Frontend gọi GET /api/account/recipient?accountNumber=...
   ↓
Backend tìm User bằng accountNumber
   ↓
Trả về RecipientResponse(accountNumber, name)
   ↓
Frontend hiển thị "Người nhận: ..."
```

### Luồng lưu lời nhắn chuyển tiền
```
User nhập message ở Fund Transfer
   ↓
Frontend gửi message trong FundTransferRequest
   ↓
AccountServiceImpl xử lý chuyển tiền
   ↓
Nếu thành công thì lưu message vào Transaction
   ↓
Transaction History hiển thị cột Lời nhắn
```

**NOTE phỏng vấn backend:** Transaction history là phần core. Email sao kê là điểm cộng, không bắt buộc để demo luồng chính.

### Kết quả đã test phần Transaction History
- App start OK, Hibernate tạo bảng `transaction` với 2 khóa ngoại `source_account_id`, `target_account_id`.
- Login account `766cc7` lấy JWT → OK.
- Deposit `100000` → 200 `"Gửi tiền thành công"` và sinh transaction `CASH_DEPOSIT`.
- Withdraw `100000` → 200 `"Rút tiền thành công"` và sinh transaction `CASH_WITHDRAWAL`.
- Transfer `100000` từ `766cc7` sang `dd3d2f` → 200 `"Chuyển tiền thành công"` và sinh transaction `CASH_TRANSFER`.
- DB kiểm tra được 3 dòng transaction mới:
  - `CASH_DEPOSIT`: source `766cc7`, target `NULL`
  - `CASH_WITHDRAWAL`: source `766cc7`, target `NULL`
  - `CASH_TRANSFER`: source `766cc7`, target `dd3d2f`
- `GET /api/account/transactions` có JWT → 200, trả list `TransactionDTO` đúng.
- `GET /api/account/recipient?accountNumber=...` có JWT → trả accountNumber + name của người nhận.
- `POST /api/account/fund-transfer` có thêm `message`; Hibernate đã thêm cột `message` vào bảng `transaction`.
- Transaction History đã bỏ cột ID và thêm cột `Lời nhắn`.

---

## ⬜ Giai đoạn 7–9 — Backlog phụ so với API gốc

Các phần này chưa bắt buộc cho demo core, nhưng nếu muốn giống `BankingPortal-API` gốc đầy đủ hơn thì làm sau:

- Logout API phía backend.
- Update user/profile API.
- OTP login.
- Forgot password/reset password bằng OTP.
- Email service: welcome email, OTP email, login notification email.
- Send bank statement email.
- Cache/idempotency cho PIN/deposit/withdraw/transfer để tránh submit lặp.
- Redis/cache config.
- Geolocation login notification.
- Swagger/OpenAPI docs.

### Plan chi tiết API còn thiếu so với project gốc

**Mục tiêu:** Bổ sung các API/hạ tầng phụ còn thiếu để project mới tiến gần `BankingPortal-API` gốc hơn. Làm theo thứ tự dưới đây để không phá flow core đã ổn.

### API bổ sung 1 — Logout

**Endpoint dự kiến:** `GET /api/users/logout`

**Mục đích:** Khi user logout, frontend xóa token localStorage; backend có thể đánh dấu token không còn dùng được nếu làm token table/revoke đầy đủ.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Kiểm tra `Token` entity hiện tại | `entity/Token.java` | Xem đã có token, expiry, account chưa |
| 2 | Tạo `TokenRepository` nếu chưa có | `repository/TokenRepository.java` | Tìm token theo chuỗi JWT |
| 3 | Thêm method logout | `service/AuthService` hoặc `UserService` | Nên để trong `AuthService` cho đúng nghiệp vụ auth |
| 4 | Xử lý revoke/delete token | `service/AuthServiceImpl` | Cách đơn giản: delete token hoặc set expired |
| 5 | Thêm controller endpoint | `controller/AuthController` hoặc `UserController` | Project gốc để `/api/users/logout`, project mới có thể cân nhắc `/api/auth/logout` |
| 6 | Sửa frontend logout | `auth.service.ts`, `main-layout.component.ts` | Gọi API logout rồi xóa localStorage |
| 7 | Test | Browser/curl | Login -> gọi API riêng -> token cũ không dùng được nếu có revoke |

**Ưu tiên:** Trung bình. Frontend hiện logout localStorage đã đủ demo, backend logout là điểm cộng.

### API bổ sung 2 — Update User/Profile

**Endpoint dự kiến:** `POST /api/users/update` hoặc `PUT /api/users/profile`

**Mục đích:** User cập nhật thông tin cá nhân: name, phoneNumber, address, countryCode. Không cho đổi password/account ở API này.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Tạo DTO request | `dto/UpdateUserRequest.java` | Không nhận trực tiếp entity `User` |
| 2 | Tạo DTO response nếu cần | `dto/UserResponse.java` | Có thể dùng lại `UserResponse` |
| 3 | Thêm method service | `service/UserService.java` | `updateUser(String accountNumber, UpdateUserRequest request)` |
| 4 | Viết logic update | `service/UserServiceImpl.java` | Lấy user từ `LoggedinUser.getAccountNumber()` rồi update field cho phép |
| 5 | Validate dữ liệu | DTO + service | Email/phone nếu cho sửa thì phải check trùng |
| 6 | Thêm controller endpoint | `controller/UserController.java` | API cần JWT |
| 7 | Thêm frontend form edit profile | `features/profile` | Có thể làm sau backend |
| 8 | Test | Browser/curl | Update xong reload profile thấy data mới |

**Ưu tiên:** Cao. Đây là tính năng thực tế, dễ nói trong phỏng vấn.

### API bổ sung 3 — OTP Login

**Endpoint gốc:**

- `POST /api/users/generate-otp`
- `POST /api/users/verify-otp`

**Mục đích:** User nhập email/accountNumber để nhận OTP qua email, sau đó nhập OTP để login và nhận JWT.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Tạo DTO OTP request | `dto/OtpRequest.java` | Nhận `identifier` |
| 2 | Tạo DTO verify OTP | `dto/OtpVerificationRequest.java` | Nhận `identifier`, `otp` |
| 3 | Tạo entity OTP | `entity/OtpInfo.java` | Lưu accountNumber, otp, generatedAt |
| 4 | Tạo repository OTP | `repository/OtpInfoRepository.java` | Tìm theo accountNumber, otp |
| 5 | Tạo OTP service | `service/OtpService.java`, `OtpServiceImpl.java` | Generate 6 số, validate hết hạn |
| 6 | Kết nối email service | `EmailService` | Gửi OTP về email thật |
| 7 | Thêm API generate OTP | `UserController` hoặc `AuthController` | Public endpoint |
| 8 | Thêm API verify OTP | `UserController` hoặc `AuthController` | Verify đúng thì trả JWT |
| 9 | Thêm frontend màn OTP login | `features/auth` | Có thể thêm sau |
| 10 | Test | Email inbox/browser | Generate OTP -> nhận mail -> verify -> login |

**Ưu tiên:** Trung bình/cao. Tốt cho phỏng vấn security/auth, nhưng mất thời gian hơn update profile/logout.

### API bổ sung 4 — Forgot Password / Reset Password

**Endpoint gốc:**

- `POST /api/auth/password-reset/send-otp`
- `POST /api/auth/password-reset/verify-otp`
- `POST /api/auth/password-reset`

**Mục đích:** User quên mật khẩu, nhận OTP qua email, verify OTP, sau đó đổi password mới.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Tạo DTO reset password | `dto/ResetPasswordRequest.java` | Nhận resetToken/newPassword |
| 2 | Tạo entity reset token | `entity/PasswordResetToken.java` | Lưu token, expiry, user/account |
| 3 | Tạo repository reset token | `repository/PasswordResetTokenRepository.java` | Tìm token hợp lệ |
| 4 | Dùng lại OTP service | `OtpService` | Không viết lại logic OTP |
| 5 | Thêm method auth service | `AuthService.java` | sendOtp, verifyOtp, resetPassword |
| 6 | Viết logic send OTP | `AuthServiceImpl.java` | Tìm user theo identifier, gửi OTP |
| 7 | Viết logic verify OTP | `AuthServiceImpl.java` | OTP đúng thì sinh reset token |
| 8 | Viết logic reset password | `AuthServiceImpl.java` | Encode BCrypt password mới |
| 9 | Thêm frontend forgot password flow | `features/auth/forgot-password` | Làm sau backend |
| 10 | Test | Browser/email | Quên mật khẩu -> OTP -> reset -> login bằng password mới |

**Ưu tiên:** Cao nếu muốn sản phẩm đầy đủ. Đây là tính năng rất thực tế.

### API bổ sung 5 — Email Service

**Không phải endpoint trực tiếp**, nhưng là hạ tầng cho OTP, welcome email, login notification, statement email.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Thêm dependency mail | `pom.xml` | `spring-boot-starter-mail` |
| 2 | Thêm config SMTP | `application.properties` | Dùng Gmail app password hoặc SMTP test |
| 3 | Tạo service interface | `service/EmailService.java` | `sendEmail(to, subject, body)` |
| 4 | Tạo service impl | `service/EmailServiceImpl.java` | Dùng `JavaMailSender` |
| 5 | Tạo template method | `EmailServiceImpl` | OTP, welcome, login notification, statement |
| 6 | Gọi email khi register | `UserServiceImpl` hoặc `UserController` | Gửi welcome email |
| 7 | Test email | Inbox thật | Cần app password/config đúng |

**Ưu tiên:** Phụ nhưng cần nếu làm OTP/reset password thật.

### API bổ sung 6 — Send Bank Statement Email

**Endpoint gốc:** `GET /api/account/send-statement`

**Mục đích:** Gửi lịch sử giao dịch của account đang login về email user.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Thêm method service | `TransactionService.java` | `sendBankStatementByEmail(String accountNumber)` |
| 2 | Viết logic lấy transaction | `TransactionServiceImpl.java` | Dùng method history hiện có |
| 3 | Format nội dung statement | `TransactionServiceImpl.java` hoặc `EmailServiceImpl` | Có amount/type/source/target/date/message |
| 4 | Lấy email user | Qua `AccountRepository` hoặc `UserRepository` | Account -> User -> email |
| 5 | Gửi email | `EmailService` | Cần API bổ sung 5 |
| 6 | Thêm controller endpoint | `AccountController.java` | `GET /api/account/send-statement` |
| 7 | Thêm frontend button | `transaction-history.component.html` | Button “Send statement” |
| 8 | Test | Browser/email | Gọi API -> nhận email statement |

**Ưu tiên:** Phụ. Làm sau Email Service.

### API bổ sung 7 — Idempotency cho giao dịch

**Mục đích:** Tránh user double click hoặc request lặp làm deposit/withdraw/transfer chạy 2 lần.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Chọn cách làm | Cache hoặc DB key | Project gốc dùng `@Cacheable` |
| 2 | Thêm cache config | `CacheConfig.java` | Có thể dùng Caffeine trước, Redis sau |
| 3 | Thêm idempotency key | Header `Idempotency-Key` hoặc hash request | Header chuyên nghiệp hơn |
| 4 | Bọc create PIN/update PIN | `AccountController.java` | Tránh submit lặp |
| 5 | Bọc deposit/withdraw/transfer | `AccountController.java` hoặc service | Quan trọng nhất |
| 6 | Frontend gửi key | `account.service.ts` | Sinh key cho mỗi submit |
| 7 | Test double click | Browser/curl | Gửi cùng key 2 lần chỉ xử lý 1 lần |

**Ưu tiên:** Cao cho phỏng vấn backend ngân hàng vì liên quan tính đúng đắn giao dịch.

### API bổ sung 8 — Redis / Cache Config

**Mục đích:** Hỗ trợ idempotency/OTP retry limit/session cache giống project gốc.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Thêm dependency Redis | `pom.xml` | `spring-boot-starter-data-redis` |
| 2 | Thêm Redis config | `config/RedisConfig.java` | Host/port/password nếu có |
| 3 | Thêm cache manager | `config/CacheConfig.java` | TTL cho idempotency/otpAttempts |
| 4 | Chạy Redis local | Docker hoặc Redis Windows | Cần hướng dẫn setup |
| 5 | Kết nối idempotency/OTP | Service/controller | Dùng cache thật |
| 6 | Test | curl/log | Cache hoạt động, TTL đúng |

**Ưu tiên:** Phụ/hạ tầng. Nếu gấp, dùng Caffeine in-memory trước.

### API bổ sung 9 — Geolocation Login Notification

**Mục đích:** Khi login thành công, gửi email thông báo thời gian/vị trí đăng nhập.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Lấy IP từ request | `HttpServletRequest` trong login | Cần truyền request vào service |
| 2 | Tạo DTO geolocation | `dto/GeolocationResponse.java` | Country/city/ip |
| 3 | Tạo service geolocation | `GeolocationService.java` | Gọi API ngoài hoặc mock |
| 4 | Gửi email login notification | `EmailService` | Cần API bổ sung 5 |
| 5 | Test | Login thật | Nếu không có network/API key thì mock location |

**Ưu tiên:** Thấp. Hay để demo security awareness, nhưng không phải core.

### API bổ sung 10 — Swagger/OpenAPI Docs

**Endpoint tài liệu:** thường là `/swagger-ui/index.html`

**Mục đích:** Có trang xem/test API khi phỏng vấn, không cần Postman.

| # | Task | Cần thêm/sửa | Ghi chú |
|---|---|---|---|
| 1 | Thêm dependency OpenAPI | `pom.xml` | `springdoc-openapi-starter-webmvc-ui` |
| 2 | Thêm config nếu cần | `config/SwaggerConfig.java` | Title, version, JWT Bearer security |
| 3 | Permit Swagger routes | `WebSecurityConfig.java` | Cho phép `/swagger-ui/**`, `/v3/api-docs/**` |
| 4 | Thêm mô tả endpoint | Controller annotations nếu muốn | Không bắt buộc |
| 5 | Test | Browser | Mở Swagger UI và gọi API có Bearer token |

**Ưu tiên:** Cao vì rất có ích khi demo phỏng vấn, làm nhanh hơn OTP/email.

### Thứ tự làm khuyến nghị cho API còn thiếu

| Thứ tự | Nhóm | Lý do |
|---|---|---|
| 1 | Swagger/OpenAPI | Nhanh, giúp demo API chuyên nghiệp |
| 2 | Update User/Profile | Tính năng thật, ít rủi ro |
| 3 | Logout backend | Hoàn thiện auth flow |
| 4 | Idempotency giao dịch | Quan trọng với banking/backend interview |
| 5 | Email Service | Nền cho OTP/reset/statement |
| 6 | Forgot Password / Reset Password | Tính năng auth thực tế |
| 7 | OTP Login | Mở rộng login, dùng lại OTP/email |
| 8 | Send Bank Statement Email | Phụ, dùng lại email/history |
| 9 | Redis/Cache Config | Làm sau khi idempotency/OTP đã rõ |
| 10 | Geolocation login notification | Nice-to-have |

### Cách làm từng API từ giờ trở đi

Mỗi API mới sẽ làm theo format:

```
1. Đọc project gốc để hiểu endpoint và flow
2. Tạo DTO request/response
3. Tạo entity/repository nếu cần lưu DB
4. Thêm method vào service interface
5. Viết service impl
6. Thêm controller endpoint
7. Thêm frontend model/service/component nếu API có màn hình
8. Test bằng curl/browser
9. Note kết quả vào ROADMAP.md
```

---

## 🔄 Frontend — Angular trong Banking-Management-System

**Mục tiêu:** Tự làm lại frontend Angular trong cùng project `Banking-Management-System/frontend`, dùng `BankingPortal-UI` chỉ để tham khảo giao diện/luồng. Không sửa project gốc `BankingPortal-UI`.

### Quy ước làm từng feature
Mỗi feature sẽ đi theo thứ tự:
```
Route -> Component -> DTO/interface -> Service gọi API -> Logic trong component -> Test trên trình duyệt
```

### 11 API backend sẽ dùng cho frontend
| # | Feature | API backend | Mục đích | Trạng thái frontend |
|---|---|---|---|---|
| 1 | Register | `POST /api/users/register` | Đăng ký user + tự tạo account | ✅ |
| 2 | Login | `POST /api/users/login` | Đăng nhập, nhận JWT | ✅ |
| 3 | Dashboard user | `GET /api/dashboard/user` | Lấy thông tin user đang login | ✅ |
| 4 | Dashboard account | `GET /api/dashboard/account` | Lấy thông tin account/số dư | ✅ |
| 5 | Check PIN | `GET /api/account/pin/check` | Kiểm tra account đã có PIN chưa | ✅ |
| 6 | Create PIN | `POST /api/account/pin/create` | Tạo PIN lần đầu | ✅ |
| 7 | Update PIN | `POST /api/account/pin/update` | Đổi PIN | ✅ |
| 8 | Deposit | `POST /api/account/deposit` | Nạp/gửi tiền | ✅ |
| 9 | Withdraw | `POST /api/account/withdraw` | Rút tiền | ✅ |
| 10 | Transfer | `POST /api/account/fund-transfer` | Chuyển khoản + lời nhắn | ✅ |
| 11 | Transaction history | `GET /api/account/transactions` | Xem lịch sử giao dịch + lời nhắn | ✅ |
| 12 | Recipient lookup | `GET /api/account/recipient` | Tra tên người nhận theo account number | ✅ |

### Checklist task frontend
| # | Việc | File/thư mục | Trạng thái |
|---|---|---|---|
| 1 | Dọn Angular template mặc định, chỉ giữ router outlet | `frontend/src/app/app.component.html` | ✅ |
| 2 | Tạo cấu trúc folder theo feature | `core/`, `features/`, `shared/` | ✅ |
| 3 | Tạo config API base URL + token key | `core/config` | ✅ |
| 4 | Tạo models/interfaces dùng chung | `core/models` | ✅ |
| 5 | Tạo AuthService: login/register/lưu token/logout | `core/services/auth.service.ts` | ✅ |
| 6 | Tạo AuthInterceptor gắn JWT vào request | `core/interceptors` | ✅ |
| 7 | Tạo AuthGuard chặn route cần đăng nhập | `core/guards` | ✅ |
| 8 | Làm Login feature | `features/auth/login` | ✅ form + route + gọi API |
| 9 | Làm Register feature | `features/auth/register` | ✅ form + route + gọi API |
| 10 | Làm layout sau login | `shared` hoặc `features/dashboard` | ✅ |
| 11 | Làm Dashboard feature | `features/dashboard` | ✅ form + route + gọi API |
| 12 | Làm PIN feature | `features/account` | ✅ form + route + gọi API |
| 13 | Làm Deposit feature | `features/account` | ✅ form + route + gọi API |
| 14 | Làm Withdraw feature | `features/account` | ✅ form + route + gọi API |
| 15 | Làm Transfer feature | `features/account` | ✅ form + route + gọi API + tra người nhận + lời nhắn |
| 16 | Làm Transaction history feature | `features/transactions` | ✅ bảng + route + gọi API + hiển thị lời nhắn |
| 17 | Test full flow: register -> login -> dashboard -> PIN -> deposit -> withdraw -> transfer -> history | trình duyệt + backend thật | ✅ |

**NOTE:** Login/register không cần JWT. Các API dashboard/account/transaction cần JWT, nên phải xong `AuthInterceptor` trước khi test những màn hình đó.

### Kết quả smoke test frontend/backend
- Angular dev server chạy OK: `http://localhost:4200/login` trả 200.
- Spring Boot backend chạy OK: `http://localhost:8180`.
- Login bằng user test `testpin_20260827_0338@gmail.com` trả JWT OK.
- Gọi API có JWT OK:
  - `GET /api/dashboard/user`
  - `GET /api/dashboard/account`
  - `GET /api/account/pin/check`
- `GET /api/account/transactions`
- `GET /api/account/recipient`
- Lưu ý khi mở frontend: nên dùng `http://localhost:4200`, không dùng `127.0.0.1`, để khớp CORS backend hiện tại.

### UI polish đã hoàn thiện

- Login/Register đã sửa logo chữ `N`, font và layout đồng bộ.
- Main layout có sidebar cố định, các màn con hiển thị qua `router-outlet`.
- Sidebar dùng icon giống hướng UI gốc hơn, Account đặt dưới PIN theo yêu cầu.
- Dashboard bỏ `Account Summary` riêng; tập trung vào lời chào, số dư, quick actions và recent activity.
- Profile hiển thị thông tin user + account.
- Account Info tách thành màn riêng.
- PIN, Deposit, Withdraw, Fund Transfer, Transaction History đã căn lại width/card/form cho đồng nhất.
- Transaction History bỏ cột ID, format tiền JPY, ngày giờ theo timezone local.

---

## ✅ Mốc đã xong — Optimize 11 API + polish UI

**Mục tiêu:** 11 API chính đã chạy được end-to-end với frontend, response/request đã đủ dùng cho UI, validation/transaction/security core đã ổn để demo.

### Backend — tối ưu 11 API hiện có
| # | Việc | Mục đích | Trạng thái |
|---|---|---|---|
| 1 | Chuẩn hóa response đủ dùng cho frontend | Login trả JSON token, dashboard/history trả DTO JSON | ✅ |
| 2 | Chuẩn hóa error response cơ bản | Exception handler trả lỗi rõ cho validation/PIN/account/user | ✅ |
| 3 | Thêm validation cho request DTO | Check amount, PIN/password không rỗng, giới hạn `10000000` | ✅ |
| 4 | Đổi register không nhận trực tiếp entity `User` | Dùng `RegisterRequest` | ✅ |
| 5 | Thêm `@Transactional` cho deposit/withdraw/transfer | Đảm bảo balance + transaction đi cùng nhau | ✅ |
| 6 | Review lại HTTP status | Sai PIN/user invalid/account not found đã có status riêng | ✅ |
| 7 | Dashboard API | Giữ tách `/dashboard/user` và `/dashboard/account` vì frontend dùng rõ ràng | ✅ |
| 8 | JWT/security core | Login JWT + interceptor + guard chạy được | ✅ |
| 9 | Thêm recipient lookup cho transfer | Hiển thị tên người nhận trước khi chuyển | ✅ |
| 10 | Thêm message chuyển tiền | Lưu và hiển thị trong transaction history | ✅ |
| 11 | Test full flow sau khi tối ưu | Backend compile + frontend build + browser manual test | ✅ |

### Frontend — polish giống UI gốc
| # | Việc | Mục đích | Trạng thái |
|---|---|---|---|
| 1 | So sánh `BankingPortal-UI` gốc | Lấy lại style/layout tốt, không sửa UI gốc | ✅ |
| 2 | Chỉnh layout/sidebar/header | App sau login nhìn giống sản phẩm hơn | ✅ |
| 3 | Chỉnh Login/Register UI | Màn hình auth đẹp và đồng nhất | ✅ |
| 4 | Chỉnh Dashboard card/số dư/account info | Dashboard dễ đọc, nổi bật thông tin chính | ✅ |
| 5 | Chỉnh form PIN/deposit/withdraw/transfer | Form gọn, rõ trạng thái loading/success/error | ✅ |
| 6 | Chỉnh bảng transaction history | Dễ scan giao dịch, format ngày/tiền rõ hơn | ✅ |
| 7 | Format tiền/ngày | Hiển thị JPY, ngày giờ local dễ đọc | ✅ |
| 8 | Đồng bộ error message theo backend mới | Frontend hiện message hợp lý theo API | ✅ |
| 9 | Responsive mobile/tablet cơ bản | UI không vỡ layout chính | ✅ |
| 10 | Test full flow bằng browser thật | Register -> login -> dashboard -> PIN -> deposit -> withdraw -> transfer -> history | ✅ |

### Thứ tự làm khuyến nghị
```
1. Core API + frontend UI đã hoàn thiện.
2. Bước tiếp theo nên làm README + screenshot demo.
3. Sau đó chọn backlog phụ nếu còn thời gian: logout, update profile, OTP/reset password, email, Swagger, idempotency.
```

---

## Hướng dẫn sử dụng

1. Mỗi lần mở máy: đọc file này để biết tiến độ.
2. Khi xong 1 mục → check ⬜ thành ✅ → commit.
3. Đối chiếu file gốc ở `D:\workspace\Project\BankingPortal-API\src\main\java\com\webapp\bankingportal\...` mỗi khi chưa rõ.
