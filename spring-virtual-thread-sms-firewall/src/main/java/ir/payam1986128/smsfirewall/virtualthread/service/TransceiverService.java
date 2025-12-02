package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsRequest;
import ir.payam1986128.smsfirewall.core.service.PhoneNumberService;
import ir.payam1986128.smsfirewall.core.smscclient.SmscApi;
import ir.payam1986128.smsfirewall.virtualthread.repository.SmsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransceiverService {

    private final SmsRepository smsRepository;
    private final PhoneNumberService phoneNumberService;
    private final SmscApi api;

    public Sms receive(Sms sms) {
        sms.setReceivedTime(LocalDateTime.now());
        sms.setSender(phoneNumberService.normalize(sms.getSender()));
        sms.setReceiver(phoneNumberService.normalize(sms.getReceiver()));
        return smsRepository.save(sms);
    }

    public Sms send(Sms sms) {
        if (Action.DONT_SEND.equals(sms.getAction())) {
            return smsRepository.save(sms);
        }
        SmsRequest request = new SmsRequest();
        request.setId(sms.getId());
        request.setSender(sms.getSender());
        request.setReceiver(sms.getReceiver());
        request.setAction(Action.valueOf(sms.getAction().name()));

        api.submitSms(request);
        sms.setSendTime(LocalDateTime.now());
        return smsRepository.save(sms);
    }
}
