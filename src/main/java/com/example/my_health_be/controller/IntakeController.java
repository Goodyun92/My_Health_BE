package com.example.my_health_be.controller;

import com.example.my_health_be.dto.common.ReturnDto;
import com.example.my_health_be.dto.intake.DailyIntakeRequestDto;
import com.example.my_health_be.dto.intake.DailyIntakeReturnDto;
import com.example.my_health_be.dto.intake.TaskIntakeRequestDto;
import com.example.my_health_be.service.IntakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/intake")
public class IntakeController {

    private final IntakeService intakeService;

    @GetMapping("/daily")
    public ReturnDto<DailyIntakeReturnDto> getDailyIntake(@RequestBody DailyIntakeRequestDto dto, Authentication authentication){
        String userName = authentication.getName();

        DailyIntakeReturnDto dailyIntakeReturnDto = intakeService.getDailyIntake(dto, userName);

        return ReturnDto.ok(dailyIntakeReturnDto);
    }

    @PostMapping("/task")
    public ReturnDto<Void> postIntakeTask(@RequestBody TaskIntakeRequestDto dto, Authentication authentication){
        String userName = authentication.getName();

        intakeService.postIntakeTask(dto,userName);

        return ReturnDto.ok();
    }

    @DeleteMapping("/task")
    public ReturnDto<Void> deleteIntakeTask(@RequestParam Long id, Authentication authentication){
        String userName = authentication.getName();

        intakeService.deleteIntakeTask(id,userName);

        return ReturnDto.ok();
    }
}
