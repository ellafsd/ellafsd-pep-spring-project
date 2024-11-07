package com.example.service;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> findMessageById(int messageId) {
        return messageRepository.findById(messageId);
    }

    public boolean existsById(int messageId) {
        return messageRepository.existsById(messageId);
    }

    public void deleteMessageById(int messageId) {
        messageRepository.deleteById(messageId);
    }

    public int updateMessageTextById(int messageId, String newMessageText) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setMessageText(newMessageText);
            messageRepository.save(message);
            return 1; // 1 row updated
        }
        return 0; // No rows updated
    }

    public List<Message> findMessagesByAccountId(int accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}
