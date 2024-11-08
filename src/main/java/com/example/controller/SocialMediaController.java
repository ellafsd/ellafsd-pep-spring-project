    package com.example.controller;
    import java.util.Optional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PatchMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import com.example.entity.Account;
    import com.example.entity.Message;
    import com.example.service.AccountService;
    import com.example.service.MessageService;
    import java.util.List;

    /**
     * You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
     * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
     * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
     * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
     */

    @RestController
    @RequestMapping({"/api", ""})   // Maps to both /api and the root path
    public class SocialMediaController {

        private final AccountService accountService;
        private final MessageService messageService;

        @Autowired
        public SocialMediaController(AccountService accountService, MessageService messageService) {
            this.accountService = accountService;
            this.messageService = messageService;
        }

        // 1. Register a new Account
        @PostMapping("/register")
        public ResponseEntity<?> registerAccount(@RequestBody Account account) {
            
        // Check if username or password is invalid
            if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password.");
        }

        // Check if the username already exists in the system
        if (accountService.findByUsername(account.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        // Save the account if it's valid and the username is unique
        Account savedAccount = accountService.saveAccount(account);
        return ResponseEntity.ok(savedAccount);
    }



        // 2. User login
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Account account) {
            Optional<Account> existingAccount = accountService.findByUsernameAndPassword(
                    account.getUsername(), account.getPassword());
            if (existingAccount.isPresent()) {
                return ResponseEntity.ok(existingAccount.get());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
            }
        }

        // 3. Create a new message
        @PostMapping("/messages")
        public ResponseEntity<?> createMessage(@RequestBody Message message) {
            // Check if the user ID exists
            if (!accountService.existsById(message.getPostedBy())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user ID.");
            }
        
            // Validate message text for length and blank content
            if (message.getMessageText() == null || message.getMessageText().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message text cannot be blank.");
            }
            if (message.getMessageText().length() > 255) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message text exceeds 255 characters.");
            }
        
            // Save the message if all validations pass
            Message savedMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(savedMessage);
        }

        
    // 4. Retrieve all messages (primary mapping with /api prefix)
    @GetMapping("/api/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.findAllMessages();
        return ResponseEntity.ok(messages);
    }

    // Additional mapping for tests that use /messages without /api prefix
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessagesWithoutApiPrefix() {
        return getAllMessages();
    }

    // 5. Retrieve a message by its ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable int messageId) {
        Optional<Message> message = messageService.findMessageById(messageId);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().body(null)); // Return 200 OK with a null body if not found
    }
        

        // 6. Delete a message by its ID
        @DeleteMapping("/messages/{messageId}")
        public ResponseEntity<?> deleteMessageById(@PathVariable int messageId) {
            if (messageService.existsById(messageId)) {
                messageService.deleteMessageById(messageId);
                return ResponseEntity.ok(1); // 1 row deleted
            } else {
                return ResponseEntity.ok().body(""); // Return 200 OK with an empty body
            }
        }
        

        // 7. Update a message text by message ID
        @PatchMapping("/messages/{messageId}")
        public ResponseEntity<?> updateMessage(@PathVariable int messageId, @RequestBody Message message) {
            if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid message text.");
            }
        
            if (!messageService.existsById(messageId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found."); // Changed to 400 Bad Request
            }
        
            int updatedRows = messageService.updateMessageTextById(messageId, message.getMessageText());
            return ResponseEntity.ok(updatedRows); // 1 row updated
        }
        

        // 8. Retrieve all messages by a particular user
        @GetMapping("/accounts/{accountId}/messages")
        public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable int accountId) {
            List<Message> messages = messageService.findMessagesByAccountId(accountId);
            return ResponseEntity.ok(messages);
        }
    }
