package it.gov.pagopa.payhub.ionotification.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utilities {
    private Utilities(){}

    public static BigDecimal longCentsToBigDecimalEuro(Long centsAmount) {
        return centsAmount != null ? BigDecimal.valueOf(centsAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN) : null;
    }

    public static String parseBigDecimalToString(BigDecimal importo) {
        if (importo == null) {
            return null;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        return decimalFormat.format(importo);
    }

    public static String centsAmountToEuroString(Long centsAmount){
        return parseBigDecimalToString(longCentsToBigDecimalEuro(centsAmount));
    }
}
