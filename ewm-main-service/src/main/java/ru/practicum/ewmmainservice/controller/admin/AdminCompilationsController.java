package ru.practicum.ewmmainservice.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.compilation.CompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmmainservice.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {

    CompilationService compilationService;

    @Autowired
    public AdminCompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Admin POST request to create Compilation");
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@RequestBody UpdateCompilationRequest compilationDto,
                                            @PathVariable Long compId) {
        log.info("Admin PATCH request to update Compilation by ID= {}", compilationDto);
        return compilationService.updateCompilation(compilationDto, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Admin DELETE request to delete Compilation by ID= {}", compId);
        compilationService.deleteCompilation(compId);
    }
}
