package it.gov.pagopa.payhub.ionotification.utils;

import org.slf4j.MDC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utilities {

    @SuppressWarnings("java:S5843")
    private static final String CF_VALIDITY_REGEX = "^(?:[A-Z][AEIOUX][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][0-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][0-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[0-9MNP-V][\\dLMNP-V]|[0L][0-9MNP-V]))[A-Z]$";
    private static final Pattern CF_PATTERN = Pattern.compile(CF_VALIDITY_REGEX);

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

    public static String getTraceId(){
        return MDC.get("traceId");
    }

    public static String getSpanId(){
        return MDC.get("spanId");
    }

    public static boolean checkFiscalCode(String fiscalCode) {
        if(fiscalCode == null) return false;
        return CF_PATTERN.matcher(fiscalCode).matches();
    }
}
