package com.aact.infraservice.controller;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.service.CapsService;
import com.aact.infraservice.service.impl.CapsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("caps")
@RequiredArgsConstructor
public class CapsController {

    private final CapsService capsService;

    @GetMapping(value = "/findIdToDate")
    public ResponseDTO<?> findIdToDate(@RequestParam("id") String id, @RequestParam("start") String start, @RequestParam("end") String end) {
        return capsService.findIdToDate(id, start, end);
    }
}
