package com.mySpringProject.OnlineBookShop.catalog.web;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        if (title.isPresent() && author.isPresent()) {
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalog.findByAuthor(author.get());
        }
        return catalog.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @Validated(UpdateValidation.class) @RequestBody RestBookCommand command) {
        CatalogUseCase.UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        catalog.updateBookCover(
                new UpdateBookCoverCommand(
                        id,
                        file.getBytes(),
                        file.getContentType(),
                        file.getOriginalFilename()
                )
        );
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalog.removeBookCover(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Validated(CreateValidation.class) @RequestBody RestBookCommand command) {
        Book book = catalog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createdBookUri(book)).build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }

    private URI createdBookUri(Book book) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    interface UpdateValidation {
    }

    interface CreateValidation {
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title", groups = {CreateValidation.class})
        private String title;

        @NotEmpty(message = "Please provide an author", groups = {CreateValidation.class})
        private Set<Long> authors;

        @NotNull(groups = {CreateValidation.class})
        private Integer year;

        @NotNull
        @PositiveOrZero
        private Long available;

        @NotNull(groups = {CreateValidation.class})
        @DecimalMin(value = "0.00", groups = {CreateValidation.class, UpdateValidation.class})
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateCommand(Long id) {
            return new UpdateBookCommand(id, title, authors, year, price);
        }
    }
}