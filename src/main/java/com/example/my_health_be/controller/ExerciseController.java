package com.example.my_health_be.controller;

import com.example.my_health_be.dto.common.ReturnDto;
import com.example.my_health_be.dto.exercise.DailyExerciseReturnDto;
import com.example.my_health_be.dto.exercise.TaskExerciseRequestDto;
import com.example.my_health_be.dto.exercise.DailyExerciseRequestDto;
import com.example.my_health_be.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/daily")
    public ReturnDto<DailyExerciseReturnDto> getDailyExercise(@RequestBody DailyExerciseRequestDto dto, Authentication authentication){
        String userName = authentication.getName();

        DailyExerciseReturnDto dailyExerciseReturnDto = exerciseService.getDailyExercise(dto,userName);

        return ReturnDto.ok(dailyExerciseReturnDto);
    }

    @PostMapping("/task")
    public ReturnDto<Void> postExerciseTask(@RequestBody TaskExerciseRequestDto dto, Authentication authentication){
        String userName = authentication.getName();

        exerciseService.postExerciseTask(dto,userName);

        return ReturnDto.ok();
    }

    @DeleteMapping("/task")
    public ReturnDto<Void> deleteExerciseTask(@RequestParam Long id, Authentication authentication){
        String userName = authentication.getName();

        exerciseService.deleteExerciseTask(id,userName);

        return ReturnDto.ok();
    }
}
