package hr.fer.zemris.rznu.lab1.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class Error {

    private Integer status;
    private String message;

    public static ResponseEntity toResponseEntity(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new Error(status.value(), message)
                );
    }
}
