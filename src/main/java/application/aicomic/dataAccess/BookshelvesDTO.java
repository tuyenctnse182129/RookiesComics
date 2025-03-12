package application.aicomic.dataAccess;

import java.util.List;

import lombok.Data;

@Data
public class BookshelvesDTO {
    private String bookshelveId;
    private String bookshelveName;
    private String description;
    private byte status;
    private List<String> comicsIds;
    private List<String> userIds;
}
