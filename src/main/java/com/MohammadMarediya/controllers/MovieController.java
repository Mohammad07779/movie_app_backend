package com.MohammadMarediya.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.MohammadMarediya.dto.MovieDto;
import com.MohammadMarediya.dto.MoviePageResponse;
import com.MohammadMarediya.exceptions.EmptyFileException;
import com.MohammadMarediya.service.MovieService;
import com.MohammadMarediya.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@CrossOrigin(origins = "*")
@Tag(name = "Movie APIs", description = "Endpoints for managing movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(
            summary = "Add a new movie (Admin only)",
            description = "Allows an admin to add a new movie along with its poster/image",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Movie created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request or empty file", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(
            @Parameter(description = "Poster file") @RequestPart MultipartFile file,
            @Parameter(description = "Movie DTO in JSON string") @RequestPart String movieDto) throws IOException, EmptyFileException {

        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty! Please send another file!");
        }
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @Operation(summary = "Get movie by ID", description = "Fetch a single movie by its ID")
    @ApiResponse(responseCode = "200", description = "Movie found")
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId) {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @Operation(summary = "Get all movies", description = "Returns a list of all movies")
    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @Operation(
            summary = "Update an existing movie",
            description = "Update movie details with optional file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content)
            }
    )
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(
            @PathVariable Integer movieId,
            @Parameter(description = "Optional updated poster") @RequestPart(required = false) MultipartFile file,
            @Parameter(description = "Updated Movie DTO in JSON string") @RequestPart String movieDtoObj) throws IOException {
        if (file == null || file.isEmpty()) file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));
    }

    @Operation(
            summary = "Delete a movie",
            description = "Deletes a movie by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content)
            }
    )
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @Operation(
            summary = "Get movies with pagination",
            description = "Retrieve paginated list of movies"
    )
    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @Operation(
            summary = "Get movies with pagination and sorting",
            description = "Retrieve movies with pagination and custom sorting options"
    )
    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String dir
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, dir));
    }

    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
