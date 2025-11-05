package ir.payam1986128.smsfirewall.core.smscclient;

import feign.Response;
import feign.codec.ErrorDecoder;

public class SmscApiErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() < 600)
            throw new IllegalArgumentException("SMSC API Call Error!");
        return new Default().decode(methodKey, response);
    }
}
