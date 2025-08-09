# Trading Management System (Hệ thống Quản lý Giao dịch)

## Mô tả dự án
Hệ thống quản lý giao dịch vàng và tiền tệ được phát triển bằng Java Swing với cơ sở dữ liệu MySQL.

## Tính năng chính
- ✅ Quản lý giao dịch vàng và tiền tệ
- ✅ Thống kê và báo cáo theo ngày/tháng
- ✅ Xuất danh sách giao dịch ra file (TXT/CSV)
- ✅ Tính toán thành tiền tự động theo loại giao dịch
- ✅ Giao diện người dùng thân thiện

## Cấu trúc dự án
```
src/
├── Main.java                     # Entry point
├── config/
│   └── DatabaseConfig.java      # Cấu hình database
├── dao/                          # Data Access Objects
│   ├── BaseDAO.java
│   ├── GiaoDichDAO.java
│   └── impl/
│       └── GiaoDichDAOImpl.java
├── dto/                          # Data Transfer Objects
│   ├── GiaoDichFormDTO.java
│   └── ThongKeDTO.java
├── exception/                    # Custom exceptions
│   ├── BusinessException.java
│   ├── DataAccessException.java
│   └── ValidationException.java
├── model/                        # Domain models
│   ├── GiaoDich.java            # Base class
│   ├── GiaoDichTienTe.java      # Currency transactions
│   └── GiaoDichVang.java        # Gold transactions
├── service/
│   └── QuanLyGiaoDich.java      # Business logic
├── ui/
│   └── MainFrame.java           # Main GUI
└── utils/                       # Utilities
    ├── DatabaseUtil.java
    └── ValidationUtil.java
```

## Công thức tính toán
### Giao dịch Vàng:
- **Thành tiền = Đơn giá × Số lượng**

### Giao dịch Tiền tệ:
- **VND**: Thành tiền = Đơn giá × Số lượng
- **Ngoại tệ khác (USD, EUR)**: Thành tiền = Đơn giá × Số lượng × Tỉ giá

## Yêu cầu hệ thống
- Java 8 trở lên
- MySQL Server
- MySQL Connector/J 8.4.0

## Cách chạy ứng dụng
1. Đảm bảo MySQL Server đang chạy
2. Tạo database theo schema trong `database_schema.sql`
3. Chạy lệnh: `run.bat` hoặc `java -cp "lib/*;build" Main`

## Tác giả
Nhóm 6 - Dự án quản lý giao dịch

## Ghi chú phiên bản
- **v1.0**: Phiên bản cơ bản với đầy đủ tính năng
- **v1.1**: Tối ưu hóa cho VND (không cần nhập tỉ giá)
