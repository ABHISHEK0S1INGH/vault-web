package vaultWeb.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vaultWeb.services.ChatImageService;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat Image Controller", description = "Handles image uploads for chat messages")
@RequiredArgsConstructor
public class ChatImageController {

  private final ChatImageService chatImageService;

  @Value("${vault.chatImage.maxSizeBytes:5242880}") // default 5 MB
  private long maxFileSizeBytes;

  @Value("${vault.chatImage.allowedMimeTypes:image/jpeg,image/png,image/gif,image/webp}")
  private String allowedMimeTypesProp;

  /**
   * Handles multipart image uploads for chat messages.
   *
   * <p>Performs basic validation on the uploaded file (non-empty, size limit, allowed MIME types)
   * and delegates to {@link vaultWeb.services.ChatImageService} to persist the image and validate
   * sender/receiver users.
   *
   * @param imageFile the uploaded multipart image file (field name: {@code image})
   * @param senderUserId the ID of the sending user (must not be null)
   * @param receiverUserId the ID of the receiving user (must not be null)
   * @return a 200 OK response containing an implementation-specific image reference string
   * @throws IllegalArgumentException if the file is empty/invalid or if IDs are null
   */
  @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Upload Chat Image",
      description = "Uploads an image to be used in chat messages.")
  public ResponseEntity<String> uploadChatImage(
      @RequestParam("image") MultipartFile imageFile,
      @RequestParam Integer senderUserId,
      @RequestParam Integer receiverUserId) {
    try {
      if (senderUserId == null) {
        throw new IllegalArgumentException("senderUserId must not be null");
      }
      if (receiverUserId == null) {
        throw new IllegalArgumentException("receiverUserId must not be null");
      }
      validateImage(imageFile);
      byte[] imageByteArray = imageFile.getBytes();
      String imageRef =
          chatImageService.uploadChatImage(imageByteArray, senderUserId, receiverUserId);
      return ResponseEntity.ok(imageRef);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void validateImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Image file cannot be empty");
    }
    if (file.getSize() > maxFileSizeBytes) {
      throw new IllegalArgumentException("Image file too large");
    }

    String contentType = file.getContentType();
    Set<String> allowed = new HashSet<>(Arrays.asList(allowedMimeTypesProp.split(",")));
    if (contentType == null || !allowed.contains(contentType)) {
      throw new IllegalArgumentException("Unsupported image type. Allowed: " + allowed);
    }
  }
}
