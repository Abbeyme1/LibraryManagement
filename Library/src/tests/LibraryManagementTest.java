package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import classes.Book;
import classes.LibraryManagement;
import exceptions.CustomException;

public class LibraryManagementTest {
	
	LibraryManagement lm;

	@BeforeEach
	void setUp()
	{
		lm = new LibraryManagement();
		
		try {
			lm.createAdmin("Admin", "admin@a.com", "password123", "jsdf,jsf jsdkfj jsfk", "7410258963");
			lm.createUser("User", "user@a.com", "password123", "jsdf,jsf jsdkfj jsfk", "7410258963");
			lm.signIn("admin@a.com", "password123");
			lm.addBook("Atomic Habits", "James Clear");
			lm.addBook("Harry Potter", "J. K. Rowling");
			lm.signOut();
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
		
	}
	
	@AfterEach
	public void cleanUp()
	{
		try {
			lm.signOut();
			Book.resetBookIdCounter();
		}
		catch (Exception e) {
		}
	}
	
	
	@Test
	public void viewBooks()
	{
		try {
			lm.signIn("user@a.com", "password123");
			HashMap<String,Book> hm = lm.viewBooks();
			
			assertEquals(2, hm.size());
			assertEquals("Atomic Habits", hm.get("b1").getTitle());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void viewBook()
	{
		try {
			lm.signIn("user@a.com", "password123");
			Book book = lm.viewBook("b1");
			
			assertEquals("Atomic Habits", book.getTitle());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void viewBookDoesNotExists()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			
			lm.signIn("user@a.com", "password123");
			lm.viewBook("b4");
		});
		
		
		String expectedMessage = "No Such Book Exists in Library";
		
		String actualMessage = e.getLocalizedMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	
	@Test
	public void getBookDoesNotExists()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			
			lm.signIn("user@a.com", "password123");
			lm.getBook("b4");
		});
		
		
		String expectedMessage = "No Such Book Exists in Library";
		
		String actualMessage = e.getLocalizedMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void getBookWithoutLogin()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			lm.getBook("b4");
		});
		
		String expectedMessage = "Please LogIn";
		
		String actualMessage = e.getLocalizedMessage();

		assertEquals(expectedMessage, actualMessage);
	}
	
	
	@Test
	public void getBook()
	{
		try {
			lm.signIn("user@a.com", "password123");
			lm.getBook("b1");
			
			// check book
			Book book = lm.viewBooks().get("b1");
			
			assertEquals(book.getBorrowedBy(), lm.currentUser.getUserId());
			
			// check user
			
			assertTrue(lm.currentUser.borrowedBook("b1"));
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void getBookAlreadyBooked()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			lm.signIn("user@a.com", "password123");
			lm.getBook("b1");
			lm.signOut();
			
			lm.signIn("admin@a.com", "password123");
			lm.getBook("b1");
		});
		
		String expectedMessage = "Already Borrowed";
		
		String actualMessage = e.getLocalizedMessage();

		assertTrue(actualMessage.contains(actualMessage));
		
	}
	
	@Test
	public void returnBookWithoutLogin()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			lm.returnBook("b1");
		});
		
		String expectedMessage = "Please LogIn";
		
		String actualMessage = e.getLocalizedMessage();
		
		assertEquals(actualMessage,expectedMessage);
	}
	
	@Test
	public void returnBookDoesNotExists()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			
			lm.signIn("user@a.com", "password123");
			lm.returnBook("b4s");
		});
		
		String expectedMessage = "No Such Book Exists in Library";
		
		String actualMessage = e.getLocalizedMessage();
		
		assertEquals(actualMessage,expectedMessage);
	}
	
	@Test
	public void returnBookNotBorrowed()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			lm.signIn("user@a.com", "password123");
			lm.returnBook("b2");
		});
		
		String expectedMessage = "Invalid Request";
		
		String actualMessage = e.getLocalizedMessage();
		
		
		assertEquals(actualMessage,expectedMessage);
	}
	
	@Test
	public void returnBook()
	{
		try {
			lm.signIn("user@a.com", "password123");
			
			lm.getBook("b1");
			lm.returnBook("b1");
			
			// check book
			Book book = lm.viewBooks().get("b1");
			
			assertEquals(book.getBorrowedBy(), null);
			
			// check user
			
			assertFalse(lm.currentUser.borrowedBook("b1"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	@Test
	public void lostBookWithoutLogin()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			lm.getBook("b4");
		});
		
		String expectedMessage = "Please LogIn";
		
		String actualMessage = e.getLocalizedMessage();

		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void lostBookDoesNotExists()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			
			lm.signIn("user@a.com", "password123");
			lm.lostBook("b4");
		});
		
		String expectedMessage = "No Such Book Exists in Library";
		
		String actualMessage = e.getLocalizedMessage();

		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void lostBookDoesBorrowedByUser()
	{
		Exception e = assertThrows(CustomException.class, () -> {
			
			lm.signIn("user@a.com", "password123");
			lm.lostBook("b2");
			
		});
		
		String expectedMessage = "Invalid Request";
		
		String actualMessage = e.getLocalizedMessage();
		

		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void lostBook()
	{
		try {
			lm.signIn("user@a.com", "password123");
			lm.getBook("b1");
			int fine = lm.currentUser.getFine();
			
			lm.lostBook("b1");
			
			// check fine
			int updateFine = lm.currentUser.getFine();
			
			assertEquals(updateFine, fine+10);
			
			
			// check bookdb
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	
	
	
}
