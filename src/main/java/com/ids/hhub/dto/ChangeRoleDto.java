package com.ids.hhub.dto;

import com.ids.hhub.model.enums.PlatformRole;
import lombok.Data;

@Data
public class ChangeRoleDto {
    private PlatformRole newRole; //USER, EVENT_CREATOR o ADMIN
}
