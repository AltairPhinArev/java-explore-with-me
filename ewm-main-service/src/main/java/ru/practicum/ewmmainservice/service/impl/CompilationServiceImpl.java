package ru.practicum.ewmmainservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.dto.compilation.CompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmainservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ValidationException;
import ru.practicum.ewmmainservice.mapper.CompilationMapper;
import ru.practicum.ewmmainservice.model.Compilation;
import ru.practicum.ewmmainservice.model.Event;
import ru.practicum.ewmmainservice.repository.CompilationRepository;
import ru.practicum.ewmmainservice.repository.EventRepository;
import ru.practicum.ewmmainservice.service.CompilationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompilationServiceImpl implements CompilationService {

    CompilationRepository compilationRepository;
    EventRepository eventRepository;

    @Lazy
    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("POST/Admin");
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank() ||
        newCompilationDto.getTitle().length() > 50 || newCompilationDto.getTitle().isEmpty()) {
            throw new ValidationException("");
        }

        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        Compilation compilation = CompilationMapper.toCompilationToCreate(newCompilationDto);
        compilation.setEvents(findEvents(newCompilationDto.getEvents()));

        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Compilation was successfully created");
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        log.info("PATCH/Admin");

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id = %d not found", compId)));

        Set<Event> events = null;

        if (updateCompilationRequest.getEvents() != null) {
            events = findEvents(updateCompilationRequest.getEvents());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (compilation.getTitle().length() > 50 || compilation.getTitle().isEmpty()) {
            throw new ValidationException("");
        }

        compilation.setId(compId);

        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id = {} was successfully updated", compId);
        return CompilationMapper.toCompilationDto(newCompilation);
    }


    @Override
    public void deleteCompilation(Long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info("Compilation with ID= {} was deleted", compId);
        } else {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).toList();
        }

        log.info("Get list of compilations by size - {}", compilations.size());
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long comId) {
        final Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation not found with id = %s", comId)));
        log.info("Get compilation: {}", compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    private Set<Event> findEvents(Set<Long> eventsId) {
        if (eventsId == null) {
            return Set.of();
        }
        return eventRepository.findAllByIdIn(eventsId);
    }
}
