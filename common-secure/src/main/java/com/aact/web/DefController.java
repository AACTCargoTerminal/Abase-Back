package com.aact.web;

import com.aact.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
public class DefController {
    @GetMapping(value = "/")
    public ResponseEntity<?> base() {
        return ResponseEntity.ok("성공");
    }
}
