package es.kiwi.drinksdispenser.infrastructure.exception.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponses {

    private LocalDateTime date;

    private String message;

    private List<String> details;

}