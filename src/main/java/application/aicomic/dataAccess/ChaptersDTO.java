package application.aicomic.dataAccess;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChaptersDTO {
    private String chapterId;
    private String chapterName;
    private String comicId;
    private LocalDateTime publishedDate;
    private String description;
    private byte status;
}
