package dev.mvvasilev.common.configuration;

import dev.mvvasilev.common.web.APIErrorDTO;
import dev.mvvasilev.common.web.APIResponseDTO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Configuration
public class CommonControllerConfiguration {

    @RestControllerAdvice(basePackages = {"dev.mvvasilev"})
    public static class APIResponseAdvice {

        @Value("${debug}")
        private boolean isDebug = false;

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ExceptionHandler(Exception.class)
        public ResponseEntity<APIResponseDTO<Object>> processGenericException(Exception ex) {
            List<APIErrorDTO> errors = List.of(
                    new APIErrorDTO(
                            ex.getMessage(),
                            isDebug ? ex.getClass().getCanonicalName() : null,
                            isDebug ? ExceptionUtils.getStackTrace(ex) : null
                    )
            );

            logger.error("Exception", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponseDTO<>(
                            null,
                            errors,
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                    ));
        }
    }

}
