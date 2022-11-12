package com.library.libraryservice.service;

import com.library.libraryservice.client.BookServiceClient;
import com.library.libraryservice.dto.AddBookRequest;
import com.library.libraryservice.dto.LibraryDto;
import com.library.libraryservice.exception.LibraryNotFoundException;
import com.library.libraryservice.model.Library;
import com.library.libraryservice.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final BookServiceClient bookServiceClient;

    public LibraryService(LibraryRepository libraryRepository,
                          BookServiceClient bookServiceClient) {
        this.libraryRepository = libraryRepository;
        this.bookServiceClient = bookServiceClient;
    }
    public LibraryDto getAllBooksInLibraryById(String id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id: " + id));

        return new LibraryDto(Objects.requireNonNull(library.getId()),
                library.getUserBook()
                        .stream()
                        .map(book -> bookServiceClient.getBookById(book).getBody())
                        .collect(Collectors.toList()));
    }

    public LibraryDto createLibrary() {
        Library newLibrary = libraryRepository.save(new Library());
        return new LibraryDto(Objects.requireNonNull(newLibrary.getId()));
    }

    public void addBookToLibrary(AddBookRequest request) {
        String bookId = Objects.requireNonNull(bookServiceClient.getBookByIsbn(request.getIsbn()).getBody()).getBookId();

        Library library = libraryRepository.findById(request.getId())
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id: " + request.getId()));

        library.getUserBook()
                .add(bookId);

        libraryRepository.save(library);
    }

    public List<String> getAllBooksInLibraries() {
        return libraryRepository.findAll()
                .stream()
                .map(Library::getId)
                .collect(Collectors.toList());
    }
}
