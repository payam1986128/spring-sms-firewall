package ir.payam1986128.smsfirewall.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {
    private final String countryCode;

    public PhoneNumberService(@Value("${app.phone-number.country-code}") String countryCode) {
        this.countryCode = normalizeCountryCode(countryCode);
    }

    private String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return null;
        }
        String result = countryCode.replace("00", "+");
        if (!result.contains("+")) {
            result = "+" + result;
        }
        return result;
    }

    public String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }
        if (phoneNumber.startsWith("00")) {
            return phoneNumber.replaceFirst("00", "+");
        }
        if (phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("0", countryCode);
        }
        return phoneNumber;
    }
}
