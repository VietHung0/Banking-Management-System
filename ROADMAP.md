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

## ⬜ Giai đoạn 6 — Transaction history + sao kê email

**Mục tiêu:** Mỗi lần deposit/withdraw/transfer thành công thì hệ thống phải lưu lại lịch sử giao dịch. Sau đó user có thể xem lịch sử giao dịch và gửi sao kê về email.

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
| 16 | Thêm dependency/config gửi email nếu làm sao kê | `pom.xml`, `application.properties` | ⬜ |
| 17 | Viết logic gửi sao kê email | `service/TransactionServiceImpl.java` | ⬜ |
| 18 | Thêm API gửi sao kê email | `controller/AccountController.java` | ⬜ |
| 19 | Test gửi sao kê email | curl/email inbox | ⬜ |
| 20 | Note kết quả test vào roadmap | `ROADMAP.md` | ⬜ |

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

### Luồng gửi sao kê email
```
Client gọi GET /api/account/send-statement có JWT
   ↓
AccountController lấy accountNumber từ LoggedinUser
   ↓
TransactionServiceImpl lấy user email + danh sách transaction
   ↓
Format nội dung sao kê
   ↓
Gửi email
```

**NOTE phỏng vấn backend:** Nếu thời gian gấp, ưu tiên xong chắc task 1–15 trước. Email sao kê là điểm cộng, nhưng transaction history mới là phần quan trọng nhất.

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

---

## ⬜ Giai đoạn 7–9 (chưa bắt đầu)
Giai đoạn 7 → 9 lặp lại **đúng kiến trúc Giai đoạn 2**, chỉ đổi nghiệp vụ (OTP, dashboard, hoàn thiện).

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
| 2 | Login | `POST /api/users/login` | Đăng nhập, nhận JWT | ⬜ |
| 3 | Dashboard user | `GET /api/dashboard/user` | Lấy thông tin user đang login | ⬜ |
| 4 | Dashboard account | `GET /api/dashboard/account` | Lấy thông tin account/số dư | ⬜ |
| 5 | Check PIN | `GET /api/account/pin/check` | Kiểm tra account đã có PIN chưa | ⬜ |
| 6 | Create PIN | `POST /api/account/pin/create` | Tạo PIN lần đầu | ⬜ |
| 7 | Update PIN | `POST /api/account/pin/update` | Đổi PIN | ⬜ |
| 8 | Deposit | `POST /api/account/deposit` | Nạp/gửi tiền | ⬜ |
| 9 | Withdraw | `POST /api/account/withdraw` | Rút tiền | ⬜ |
| 10 | Transfer | `POST /api/account/fund-transfer` | Chuyển khoản | ⬜ |
| 11 | Transaction history | `GET /api/account/transactions` | Xem lịch sử giao dịch | ⬜ |

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
| 10 | Làm layout sau login | `shared` hoặc `features/dashboard` | ⬜ |
| 11 | Làm Dashboard feature | `features/dashboard` | ⬜ |
| 12 | Làm PIN feature | `features/account` | ⬜ |
| 13 | Làm Deposit feature | `features/account` | ⬜ |
| 14 | Làm Withdraw feature | `features/account` | ⬜ |
| 15 | Làm Transfer feature | `features/account` | ⬜ |
| 16 | Làm Transaction history feature | `features/transactions` | ⬜ |
| 17 | Test full flow: register -> login -> dashboard -> PIN -> deposit -> withdraw -> transfer -> history | trình duyệt + backend thật | ⬜ |

**NOTE:** Login/register không cần JWT. Các API dashboard/account/transaction cần JWT, nên phải xong `AuthInterceptor` trước khi test những màn hình đó.

---

## Hướng dẫn sử dụng

1. Mỗi lần mở máy: đọc file này để biết tiến độ.
2. Khi xong 1 mục → check ⬜ thành ✅ → commit.
3. Đối chiếu file gốc ở `D:\workspace\Project\BankingPortal-API\src\main\java\com\webapp\bankingportal\...` mỗi khi chưa rõ.
