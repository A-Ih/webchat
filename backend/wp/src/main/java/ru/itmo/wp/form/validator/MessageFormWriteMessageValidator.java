package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.MessageForm;
import ru.itmo.wp.service.JwtService;
import ru.itmo.wp.service.MessageService;

import javax.annotation.Nonnull;

@Component
public class MessageFormWriteMessageValidator implements Validator {
    private final JwtService jwtService;
    private final MessageService messageService;

    public MessageFormWriteMessageValidator(JwtService jwtService, MessageService messageService) {
        this.jwtService = jwtService;
        this.messageService = messageService;
    }

    public boolean supports(@Nonnull Class<?> clazz) {
        return MessageForm.class.equals(clazz);
    }

    public void validate(@Nonnull Object target, Errors errors) {
        if (!errors.hasErrors()) {
            MessageForm messageForm = (MessageForm) target;
            User user = jwtService.find(messageForm.getJwt());
            if (user == null) {
                errors.rejectValue("jwt", "jwt.is-invalid", "invalid jwt received");
                return;
            }

            String addresseeIdStr = messageForm.getAddresseeId();
            if (addresseeIdStr == null || addresseeIdStr.isEmpty()) {
                // then it's just a message
                return;
            }

            long addresseeId;
            try {
                addresseeId = Long.parseLong(addresseeIdStr);
            } catch (NumberFormatException e) {
                errors.rejectValue("addresseeId", "addresseeId.is-invalid",
                        "invalid message id");
                return;
            }
            if (messageService.find(addresseeId) == null) {
                errors.rejectValue("addresseeId", "addresseeId.is-non-existent",
                        "message id doesn't exist");
            }
        }
    }
}
