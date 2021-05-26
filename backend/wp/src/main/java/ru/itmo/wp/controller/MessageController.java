package ru.itmo.wp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Message;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.MessageForm;
import ru.itmo.wp.form.validator.MessageFormWriteMessageValidator;
import ru.itmo.wp.service.JwtService;
import ru.itmo.wp.service.MessageService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/1")
public class MessageController {
    private final MessageService messageService;
    private final JwtService jwtService;
    private final MessageFormWriteMessageValidator messageFormWriteMessageValidator;

    public MessageController(MessageService messageService, JwtService jwtService, MessageFormWriteMessageValidator messageFormWriteMessageValidator) {
        this.messageService = messageService;
        this.jwtService = jwtService;
        this.messageFormWriteMessageValidator = messageFormWriteMessageValidator;
    }

    @InitBinder("messageForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(messageFormWriteMessageValidator);
    }

    @PostMapping("writeMessage")
    public void writeMessage(@Valid @RequestBody MessageForm messageForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        User user = jwtService.find(messageForm.getJwt());

        Message message = new Message();
        message.setUser(user);
        message.setText(messageForm.getText());

        String addresseeId = messageForm.getAddresseeId();
        if (addresseeId != null && !addresseeId.isEmpty()) {
            message.setAddressee(messageService.find(Long.parseLong(addresseeId)));
        }

        user.addMessage(message);
        messageService.save(message);

    }

    @GetMapping("messages")
    public List<Message> findPosts() {
        return messageService.findAll();
    }
}
