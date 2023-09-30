package ru.practicum.ewmmainservice.mapper;

import ru.practicum.ewmmainservice.dto.compilation.CompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmainservice.model.Compilation;

public class CompilationMapper {

    public static Compilation toCompilationToCreate(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .id(null)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(null)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents())
                .build();
    }

    public static Compilation toCompilation(CompilationDto compilationDto) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .events(compilationDto.getEvents())
                .build();
    }
}