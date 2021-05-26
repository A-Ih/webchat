package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Message;
import ru.itmo.wp.repository.MessageRepository;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> findAll() {
        return messageRepository.findAllByOrderByCreationTimeDesc();
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Message find(long id) {
        return messageRepository.findById(id);
    }
}
