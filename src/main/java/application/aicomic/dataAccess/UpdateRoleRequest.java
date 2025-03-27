package application.aicomic.dataAccess;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String userId;
    private byte newRoleByte;
}