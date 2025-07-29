package meety.services;

import lombok.RequiredArgsConstructor;
import meety.dtos.ChatMessageDto;
import meety.exceptions.notfound.GroupNotFoundException;
import meety.models.ChatMessage;
import meety.models.Group;
import meety.models.PrivateChat;
import meety.models.User;
import meety.repositories.ChatMessageRepository;
import meety.repositories.GroupRepository;
import meety.repositories.PrivateChatRepository;
import meety.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PrivateChatRepository privateChatRepository;

    /**
     * Saves a chat message to the database.
     *
     * @param dto DTO containing the content, sender ID, and group ID.
     * @return The persisted ChatMessage entity.
     */
    public ChatMessage saveMessage(ChatMessageDto dto) {
        User sender;

        if (dto.getSenderId() != null) {
            sender = userRepository.findById(dto.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found by ID"));
        } else if (dto.getSenderUsername() != null) {
            sender = userRepository.findByUsername(dto.getSenderUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found by username"));
        } else {
            throw new IllegalArgumentException("Sender information missing");
        }

        ChatMessage message = new ChatMessage();
        message.setContent(dto.getContent());
        message.setSender(sender);

        if (dto.getTimestamp() != null) {
            message.setTimestamp(Instant.parse(dto.getTimestamp()));
        } else {
            message.setTimestamp(Instant.now());
        }

        if (dto.getGroupId() != null) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new GroupNotFoundException("Group with id " + dto.getGroupId() + " not found"));
            message.setGroup(group);
        } else if (dto.getPrivateChatId() != null) {
            PrivateChat privateChat = privateChatRepository.findById(dto.getPrivateChatId())
                    .orElseThrow(() -> new IllegalArgumentException("PrivateChat not found"));
            message.setPrivateChat(privateChat);
        } else {
            throw new IllegalArgumentException("Either groupId or privateChatId must be provided");
        }

        return chatMessageRepository.save(message);
    }
}
