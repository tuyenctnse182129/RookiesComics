package application.aicomic.dataAccess;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private int month;
    private int year;
    private double totalRevenue;
}
