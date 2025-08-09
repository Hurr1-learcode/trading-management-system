
CREATE DATABASE IF NOT EXISTS quanly_giaodich 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE quanly_giaodich;

-- Create main table for transactions
CREATE TABLE IF NOT EXISTS giao_dich (
    ma_giao_dich VARCHAR(20) PRIMARY KEY,
    ngay_giao_dich DATE NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL CHECK (don_gia > 0),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    loai_giao_dich ENUM('VANG', 'TIEN_TE') NOT NULL,
    
    -- Fields for gold transactions
    loai_vang VARCHAR(50) NULL,
    
    -- Fields for currency transactions
    loai_tien VARCHAR(10) NULL,
    ti_gia DECIMAL(10,4) NULL CHECK (ti_gia > 0),
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_vang_fields 
        CHECK (
            (loai_giao_dich = 'VANG' AND loai_vang IS NOT NULL AND loai_tien IS NULL AND ti_gia IS NULL) OR
            (loai_giao_dich = 'TIEN_TE' AND loai_tien IS NOT NULL AND ti_gia IS NOT NULL AND loai_vang IS NULL)
        ),
    
    CONSTRAINT chk_tien_format
        CHECK (loai_tien IS NULL OR CHAR_LENGTH(loai_tien) = 3)
);

-- Create indexes for better performance
CREATE INDEX idx_giao_dich_ngay ON giao_dich(ngay_giao_dich);
CREATE INDEX idx_giao_dich_loai ON giao_dich(loai_giao_dich);
CREATE INDEX idx_giao_dich_don_gia ON giao_dich(don_gia);
CREATE INDEX idx_giao_dich_composite ON giao_dich(loai_giao_dich, ngay_giao_dich);

-- Insert sample data
INSERT INTO giao_dich (ma_giao_dich, ngay_giao_dich, don_gia, so_luong, loai_giao_dich, loai_vang) VALUES
('GD001', '2024-01-15', 2500000.00, 5, 'VANG', '24K'),
('GD002', '2024-01-16', 2400000.00, 3, 'VANG', '18K'),
('GD003', '2024-01-17', 1200000000.00, 2, 'VANG', '999.9');

INSERT INTO giao_dich (ma_giao_dich, ngay_giao_dich, don_gia, so_luong, loai_giao_dich, loai_tien, ti_gia) VALUES
('GD004', '2024-01-18', 25000.00, 100, 'TIEN_TE', 'USD', 24500.00),
('GD005', '2024-01-19', 1100.00, 50, 'TIEN_TE', 'EUR', 26800.00),
('GD006', '2024-01-20', 80000000.00, 1, 'TIEN_TE', 'JPY', 165.50);

-- Create view for statistics
CREATE VIEW v_thong_ke_giao_dich AS
SELECT 
    loai_giao_dich,
    COUNT(*) as so_luong,
    SUM(CASE 
        WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong
        WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia
    END) as tong_thanh_tien,
    AVG(CASE 
        WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong
        WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia
    END) as trung_binh_thanh_tien,
    MIN(don_gia) as don_gia_min,
    MAX(don_gia) as don_gia_max
FROM giao_dich
GROUP BY loai_giao_dich;

-- Show table structure
DESCRIBE giao_dich;

-- Show sample data
SELECT * FROM giao_dich ORDER BY ngay_giao_dich DESC;

-- Show statistics
SELECT * FROM v_thong_ke_giao_dich;
