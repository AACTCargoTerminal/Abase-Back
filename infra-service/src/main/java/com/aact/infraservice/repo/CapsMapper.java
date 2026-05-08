package com.aact.infraservice.repo;

import com.aact.infraservice.dto.CapsEnterDTO;
import com.aact.infraservice.dto.CapsUserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CapsMapper {

    List<CapsUserDTO> findUsersWithGroup(@Param("start") String start, @Param("end") String end,
                                         @Param("name") String name, @Param("id") String id, @Param("modes") String[] modes);

    List<CapsEnterDTO> findEnterToUser(@Param("id") String id, @Param("modes") String[] modes,
                                       @Param("start") String start, @Param("end") String end);

    List<CapsEnterDTO> findDateToId(@Param("id") String id, @Param("date") String date);
}
