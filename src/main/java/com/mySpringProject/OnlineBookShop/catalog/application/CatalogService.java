package com.mySpringProject.OnlineBookShop.catalog.application;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.db.AuthorJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.db.BookJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Author;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.catalog.domain.ISBN;
import com.mySpringProject.OnlineBookShop.uploads.application.ports.UploadUseCase;
import com.mySpringProject.OnlineBookShop.uploads.application.ports.UploadUseCase.SaveUploadCommand;
import com.mySpringProject.OnlineBookShop.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository repository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase upload;

    @Override
//    public List<Book> findAll() {
//        return repository.findAll();
//    }
    public List<Book> findAll() {
        return repository.findAllEager();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return repository.findAll()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findByAuthor(author);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                .stream()
                //TODO Szymon fix looking through set
//                .filter(book -> book.getAuthors().stream()
//                        .filter(authFilter -> author.toLowerCase().equals(authFilter.toString().toLowerCase()))
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Book addBook(CreateBookCommand command) {
        Book book = toBook(command);
        return repository.save(book);
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(
                command.getTitle(),
                command.getYear(),
                command.getPrice(),
                command.getAvailable());
        Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
        updateBooks(book, authors);
        return book;
    }


    private void updateBooks(Book book, Set<Author> authors) {
        book.removeAuthors();
        authors.forEach(book::addAuthor);
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors
                .stream()
                .map(authorId -> authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to find author with id: " + authorId))
                )
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository
                .findById(command.getId())
                .map(book -> {
                    updateFields(command, book);
//                    repository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Collections.singletonList("Book not found with id: " + command.getId())));
    }

    private Book updateFields(UpdateBookCommand command, Book book) {
        if (command.getTitle() != null) {
            book.setTitle(command.getTitle());
        }
        if (command.getAuthors() != null && command.getAuthors().size() > 0) {
            updateBooks(book, fetchAuthorsByIds(command.getAuthors()));
        }
        if (command.getYear() != null) {
            book.setYear(command.getYear());
        }
        if (command.getPrice() != null) {
            book.setPrice(command.getPrice());
        }
        return book;
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        int length = command.getFile().length;
        System.out.println("Received cover command: " + command.getFilename() + " bytes: " + length);
        repository.findById(command.getId())
                .ifPresent(book -> {
                    Upload savedUpload = upload.save(
                            new SaveUploadCommand(command.getFilename(), command.getFile(), command.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    repository.save(book);
                });
    }

    @Override
    public void removeBookCover(Long id) {
        repository.findById(id)
                .ifPresent(book -> {
                    if (book.getCoverId() != null) {
                        upload.removeById(book.getCoverId());
                        book.setCoverId(null);
                        repository.save(book);
                    }
                });
    }
}