package com.broadenit.broadenit.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApiResponse {
    private String message;
    private boolean status;


}
