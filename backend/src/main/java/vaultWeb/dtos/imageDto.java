package vaultWeb.dtos;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class imageDto {
    private Long id;
    private String file_name;
    private String content_type; // image/png, image/jpeg, etc.
    private int sender_id;
    private byte[] data;
}

