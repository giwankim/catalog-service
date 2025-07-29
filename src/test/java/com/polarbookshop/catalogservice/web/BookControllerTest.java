package com.polarbookshop.catalogservice.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(BookController.class)
@RequiredArgsConstructor
class BookControllerTest {

  final MockMvcTester mvcTester;

  @MockitoBean BookService bookService;

  @Test
  void whenGetBookNotExistsThenShouldReturn404() {
    String isbn = "73737313940";
    when(bookService.viewBookDetails(isbn)).thenThrow(BookNotFoundException.class);

    assertThat(mvcTester.get().uri("/books/{isbn}", isbn)).hasStatus(HttpStatus.NOT_FOUND);
  }
}
