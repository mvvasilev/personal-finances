package dev.mvvasilev.statements.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.statements.dto.SupportedMappingConversionDTO;
import dev.mvvasilev.statements.enums.MappingConversionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/enums")
public class EnumsController extends AbstractRestController {

    @GetMapping("/supported-conversions")
    public ResponseEntity<APIResponseDTO<Collection<SupportedMappingConversionDTO>>> fetchTransactionMappings() {
        return ok(Arrays.stream(MappingConversionType.values()).map(conv -> new SupportedMappingConversionDTO(
                conv,
                conv.getFrom(),
                conv.getTo()
        )).toList());
    }

}
