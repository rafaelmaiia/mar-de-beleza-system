package br.com.rafaelmaia.mar_de_beleza_system.controllers.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String path;
}
