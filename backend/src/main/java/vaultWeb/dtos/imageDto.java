package vaultWeb.dtos;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class imageDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String file_name;
    private String content_type; // image/png, image/jpeg, etc.
    private int sender_id;

    @Lob
    @Column(name = "image_content")
    private byte[] data;


}

