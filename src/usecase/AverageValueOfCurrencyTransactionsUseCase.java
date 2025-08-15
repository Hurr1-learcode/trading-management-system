package usecase;

import java.math.BigDecimal;
import java.util.List;

import exception.BusinessException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import service.QuanLyGiaoDich;

/**
 * UC10: Tính trung bình thành tiền các giao dịch tiền tệ
 * Clean Architecture UseCase
 */
public class AverageValueOfCurrencyTransactionsUseCase {
    private final QuanLyGiaoDich quanLyGiaoDich;

    public AverageValueOfCurrencyTransactionsUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        if (quanLyGiaoDich == null) throw new IllegalArgumentException("Service không được null");
        this.quanLyGiaoDich = quanLyGiaoDich;
    }

    /**
     * Tính trung bình thành tiền các giao dịch tiền tệ
     * @return BigDecimal trung bình, nếu không có giao dịch trả về BigDecimal.ZERO
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public BigDecimal execute() throws BusinessException {
        try {
            List<GiaoDich> all = quanLyGiaoDich.getAll();
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;
            for (GiaoDich gd : all) {
                if (gd instanceof GiaoDichTienTe) {
                    sum = sum.add(gd.tinhThanhTien());
                    count++;
                }
            }
            if (count == 0) return BigDecimal.ZERO;
            return sum.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tính trung bình thành tiền tiền tệ: " + ex.getMessage());
        }
    }
}
