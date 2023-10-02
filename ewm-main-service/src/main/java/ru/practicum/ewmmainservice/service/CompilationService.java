package ru.practicum.ewmmainservice.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewmmainservice.dto.compilation.CompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest compilationDto, @PathVariable Long compId);

    void deleteCompilation(Long compId);

    CompilationDto getById(Long comId);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);
}
