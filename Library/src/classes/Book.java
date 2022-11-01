package classes;

import java.time.LocalDate;

import exceptions.CustomException;

public class Book {

	private final String bookId;
	protected String title;
	protected String author;
	protected String borrowedBy; // userId
	protected LocalDate lastDateToReturn;
	private static int bookIdCounter = 1; 
	
	public Book(String title, String author) throws Exception{
		
		setTitle(title);
		setAuthor(author);
		bookId =  "b" + (bookIdCounter++);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) throws CustomException {
		
		title = title.strip();
		
		if(title.length() < 3 || title.length() > 50) throw new CustomException("Enter valid title");
		
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) throws CustomException {
		
		author = author.strip();
		
		if(author.length() < 3 || author.length() > 50) throw new CustomException("Enter valid title");
		
		this.author = author;
	}

	public String getBorrowedBy() {
		return borrowedBy;
	}

	public void setBorrowedBy(String borrowedBy) {
		this.borrowedBy = borrowedBy;
	}

	public LocalDate getLastDateToReturn() {
		return lastDateToReturn;
	}

	public void setLastDateToReturn(LocalDate lastDayToReturn) {
		
		this.lastDateToReturn = lastDayToReturn;
	}
	
	public void returnBook()
	{
		setBorrowedBy(null);
		setLastDateToReturn(null);
	}

	public String getBookId() {
		return bookId;
	}
	
	public static void resetBookIdCounter()
	{
		bookIdCounter = 1;
	}
	
	

	public String toString()
	{
		String s = "BookId: "+ getBookId() + ", Title: "+ getTitle() + ", Auhtor: "+ getAuthor() +", BorrowedBy: "+ getBorrowedBy() + ", lastDateToReturn: "+ getLastDateToReturn();
		return s;
	}


}
