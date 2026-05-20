package com.inf.medical_record_system.dto;

import com.inf.medical_record_system.data.entity.RoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private RoleName roleName;
}