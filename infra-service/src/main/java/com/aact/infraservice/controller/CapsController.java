package com.aact.infraservice.controller;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.service.CapsService;
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

    @GetMapping(value = "/findUsersWithGroup")
    public ResponseDTO<?> findUsersWithGroup(@RequestParam("start") String start, @RequestParam("end") String end,
                                             @RequestParam("name") String name, @RequestParam("id") String id, @RequestParam("modes") String[] modes) {
        return capsService.findUsersWithGroup(start, end, name, id, modes);
    }

    // findEnterToUser
    @GetMapping(value = "/findEnterToUser")
    public ResponseDTO<?> findEnterToUser(@RequestParam("id") String id, @RequestParam("modes") String[] modes,
                                          @RequestParam("start") String start, @RequestParam("end") String end) {
        return capsService.findEnterToUser(id, modes, start, end);
    }
}
