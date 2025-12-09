package vaultWeb.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter

public class ChatImageDto {
    private Long id;

    private byte[] imageContent;

    private Integer senderId;

    private Integer receiverId;

    @NotNull
    private OffsetDateTime createdon;
}
