package com.polarbookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock BookRepository bookRepository;

  @InjectMocks BookService bookService;

  @Test
  void whenBookCreateAlreadyExistsThenFail() {
    Book book = Book.create("1234567890", "Title", "Author", 9.90, "Polarsophia");
    when(bookRepository.existsByIsbn(book.isbn())).thenReturn(true);

    assertThatThrownBy(() -> bookService.addBookToCatalog(book))
        .isInstanceOf(BookAlreadyExistsException.class)
        .hasMessage("A book with ISBN " + book.isbn() + " already exists.");
  }

  @Test
  void whenBookToReadDoesNotExistsThenFail() {
    String isbn = "1234567890";
    when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.viewBookDetails(isbn))
        .isInstanceOf(BookNotFoundException.class);
  }
}
