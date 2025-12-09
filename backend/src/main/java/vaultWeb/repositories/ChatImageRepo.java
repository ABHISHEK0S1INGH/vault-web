package vaultWeb.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vaultWeb.dtos.ChatImageDto;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ChatImageRepo extends CrudRepository<ChatImageDto, Long> {


    @Transactional
    @Query(
            value = "INSERT INTO chat_images (image_content, receiver_id, sender_id, createdon) " +
                    "VALUES (CAST(:imageData AS bytea), :receiverId, :senderId, :createdon) RETURNING id",
            nativeQuery = true
    )
    Long saveImage(
            @Param("imageData") byte[] imageData,
            @Param("senderId") Integer senderId,
            @Param("receiverId") Integer receiverId,
            @Param("createdon") OffsetDateTime createdon
    );

    List<ChatImageDto> createdon(OffsetDateTime createdon);
}
