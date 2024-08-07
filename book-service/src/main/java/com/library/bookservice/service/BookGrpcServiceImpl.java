package com.library.bookservice.service;

import com.library.bookservice.dto.BookId;
import com.library.bookservice.dto.BookIdDto;
import com.library.bookservice.dto.BookServiceGrpc;
import com.library.bookservice.dto.Isbn;
import com.library.bookservice.exception.BookNotFoundException;
import com.library.bookservice.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BookGrpcServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BookGrpcServiceImpl.class);
    private final BookRepository bookRepository;

    public BookGrpcServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void getBookIdByIsbn(Isbn isbn, StreamObserver<BookId> responseObserver) {
        logger.info("Grpc call received: " + isbn.getIsbn());
        BookIdDto bookId = bookRepository.getBookByIsbn(isbn.getIsbn())
                .map(book -> new BookIdDto(book.getId(), book.getIsbn()))
                .orElseThrow(() -> new BookNotFoundException("Book could not found by isbn: " + isbn));
        responseObserver.onNext(
                BookId.newBuilder()
                        .setBookId(bookId.getBookId())
                        .setIsbn(bookId.getIsbn())
                        .build());
        responseObserver.onCompleted();
    }
}
