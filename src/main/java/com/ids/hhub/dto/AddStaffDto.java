package com.ids.hhub.dto;

import com.ids.hhub.model.enums.StaffRole;
import lombok.Data;

@Data
public class AddStaffDto {
    private Long userId;
    private StaffRole role;
}
