package application.aicomic.dataAccess;

import lombok.Data;

@Data
public class GenresDTO {
    private String genresId;
    private String genresName;
    private String genresDescription;
    private byte status;
}
