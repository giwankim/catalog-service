package com.polarbookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BookValidationTest {

  private static Validator validator;

  @BeforeAll
  static void beforeAll() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    Book book = Book.create("1234567890", "Title", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbnNotDefinedThenValidationFails() {
    Book book = Book.create("", "Title", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(2);
    List<String> constraintViolationMessages =
        violations.stream().map(ConstraintViolation::getMessage).toList();
    assertThat(constraintViolationMessages)
        .contains("The book ISBN must be defined.")
        .contains("The ISBN format must be valid.");
  }

  @Test
  void whenIsbnDefinedButIncorrect() {
    var book = Book.create("a234567890", "Title", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The ISBN format must be valid.");
  }

  @Test
  void whenTitleIsNotDefined() {
    var book = Book.create("1234567890", "", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book title must be defined.");
  }

  @Test
  void whenAuthorIsNotDefined() {
    Book book = Book.create("1234567890", "Title", "", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book author must be defined.");
  }

  @Test
  void whenPriceIsNotDefined() {
    Book book = Book.create("1234567890", "Title", "Author", null, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be defined.");
  }

  @Test
  void whenPriceDefinedButZero() {
    Book book = Book.create("1234567890", "Title", "Author", 0.0, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be greater than zero.");
  }

  @Test
  void whenPriceDefinedButNegative() {
    Book book = Book.create("1234567890", "Title", "Author", -9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be greater than zero.");
  }

  @Test
  void whenPublisherIsNotDefinedThenValidationSucceeds() {
    Book book = Book.create("1234567890", "Title", "Author", 9.90, null);
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).isEmpty();
  }
}
