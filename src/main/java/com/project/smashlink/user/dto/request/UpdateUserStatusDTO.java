package com.project.smashlink.user.dto.request;

import com.project.smashlink.user.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserStatusDTO {

    @NotNull(message = "Status is requred")
    private UserStatus status;

}
