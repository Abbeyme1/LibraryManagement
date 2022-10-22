package classes;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import exceptions.CustomException;


public class LibraryManagement {

	public User currentUser;
	HashMap<String, User> userDb;
	HashMap<String, Book> booksDb;
	public List<Log> logBook;
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RED= "\u001B[31m";
	private static final String ANSI_RESET = "\u001B[0m";
	
	public LibraryManagement()
	{
		booksDb = new HashMap<>();
		userDb = new HashMap<>();
		currentUser = null;
		
	}
	
	public void getBook(String bookId) throws Exception
	{
		if(currentUser == null) throw new CustomException("Please LogIn");
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		
		
		Book book = booksDb.get(bookId);
		if(book.getBorrowedBy() != null) throw new CustomException("Already Borrowed, You can have it after " + book.getLastDateToReturn());
		
		LocalDate lastDayToReturn = LocalDate.now().plusDays(7);
		
		// update user
		currentUser.addBook(bookId);
		
		// update book
		book.setBorrowedBy(currentUser.getUserId());
		book.setLastDateToReturn(lastDayToReturn);
		
	}
	
	public void lostBook(String bookId) throws Exception
	{
		if(currentUser == null) throw new CustomException("Please LogIn");
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		
		if(!currentUser.borrowedBook(bookId)) throw new CustomException("Invalid Request");
		
		
		// fine
		currentUser.addFine(10);
		
		// update booksdb
		booksDb.remove(bookId);
	}
	
	public void addToFavourites(String bookId) throws Exception
	{
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		if(currentUser == null) throw new CustomException("Please LogIn");
		
		currentUser.addFavourites(bookId);
		
	}
	
	
	public void returnBook(String bookId) throws Exception
	{
		if(currentUser == null) throw new CustomException("Please LogIn");
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		
		if(!currentUser.borrowedBook(bookId)) throw new CustomException("Invalid Request");
		
		Book book = booksDb.get(bookId);
		if(book.getBorrowedBy() == null) throw new CustomException("Invalid Request");
		
		// update user
		currentUser.returnBook(bookId);
		
		// update book
		book.returnBook();
		
	}
	
	public void addBook(String title, String author) throws Exception
	{
		if(!currentUser.isAdmin()) throw new CustomException("Unauhtorized");
		
		Book book = new Book(title, author);
		
		booksDb.put(book.getBookId(), book);
	}
	
	public void removeBook(String bookId) throws Exception
	{
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		checkAuthorization();
		
		booksDb.remove(bookId);
	}

	public void checkAuthorization() throws CustomException
	{
		if(!currentUser.isAdmin()) throw new CustomException("Unauhtorized");
	}
	
	public void updateBook(String bookId,String borrowedBy, LocalDate lastDateToReturn) throws Exception
	{
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		checkAuthorization();
		
		Book book = booksDb.get(bookId);
		book.setBorrowedBy(borrowedBy);
		book.setLastDateToReturn(lastDateToReturn);
	}
	
	public HashMap<String,Book> viewBooks()
	{
		return booksDb;
	}
	
	public Book viewBook(String bookId) throws Exception
	{
		if(!booksDb.containsKey(bookId)) throw new CustomException("No Such Book Exists in Library");
		
		return booksDb.get(bookId);
	}
	
	public HashMap<String,Book> getBooks()
	{
		return viewBooks();
	}
	
	
	
	
	
	
	private void checkEmail(String email) throws CustomException
	{
		if(userDb.containsKey(email)) throw new CustomException("Email already exists");
	}
	
	
	public User createUser(String name, String email, String password, String address, String phoneNumber) throws Exception {
		
		try {
			
			// checkEmail: temp. here
			checkEmail(email);
						
			User u = new User(name,email,password, address, phoneNumber);
			userDb.put(u.getEmail(), u);
			
			return u;
		}
		catch(Exception e)
		{
			CustomException c = new CustomException("Enter valid attributes");
			c.initCause(e);
			
			throw c;
		}
		
	}
	
	public User createAdmin(String name, String email, String password, String address, String phoneNumber) throws Exception {
		
		User u = createUser(name, email, password, address, phoneNumber);
		u.setAdmin(true);
		return u;
		
	}
	
	public void signUp(String name, String email, String password, String address, String phoneNumber)
	{
		try {
			currentUser = createUser(name, email, password, address, phoneNumber);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void signIn(String email, String password) throws Exception
	{
		
		if(!userDb.containsKey(email)) throw new CustomException("Incorrect Email/Password");
		
		User user = userDb.get(email);
		
//		System.out.println(user.toString());
		
		if(!user.checkPassword(password)) throw new CustomException("Incorrect Email/Password");
		
		currentUser = user;
		
//		System.out.println(ANSI_GREEN + "SignedIn successfully" + ANSI_RESET);
		
		
	}
	
	public void signOut()
	{
		currentUser = null;
//		System.out.println(ANSI_RED + "SignedOut successfully" + ANSI_RESET);
		
		
	}

	public void deleteUser() throws Exception 
	{
		if(currentUser == null) throw new CustomException("Invalid Request");
		
		userDb.remove(currentUser.getEmail());
		
		currentUser = null;
		
	}
	
	public void deleteUser(String email)  throws Exception 
	{
		if(currentUser == null) throw new CustomException("Invalid Request");
		
		if(currentUser.getEmail() == email) deleteUser();
		else
		{
			if(!currentUser.isAdmin()) throw new CustomException("Unauthorized");
			
			if(!userDb.containsKey(email)) throw new CustomException("Invalid Request");
			User user = userDb.get(email);
			
			userDb.remove(user.getEmail());
			
		}
		
	}

	
	public void grantAdminAccess(String email) throws CustomException
	{
		if(currentUser == null || !currentUser.isAdmin()) throw new CustomException("Unauthorized");
		
		if(!userDb.containsKey(email)) throw new CustomException("Invalid Request");
		User user = userDb.get(email);
		
		user.setAdmin(true);
	}
	
	public void accessUsers() throws CustomException
	{
		if(currentUser == null || !currentUser.isAdmin()) throw new CustomException("Unauthorized");
		for(String key : userDb.keySet())
		{
			System.out.println(userDb.get(key).toString());
		}
		
	}
	
	private void update(User user, String newName, String newEmail,String newAddress, String newPhoneNumber) throws CustomException
	{
		
		user.setName(newName);
		
		user.setEmail(newEmail);
		
		user.setAddress(newAddress);
		
		user.setPhoneNumber(newPhoneNumber);
		
	}
	
	public void updateUserDetails(String userEmail, String newName, String newEmail,String newAddress, String newPhoneNumber) throws CustomException
	{
		if(currentUser.getEmail() == userEmail) update(currentUser, newName, newEmail, newAddress, newPhoneNumber);
		else
		{
			if(!currentUser.isAdmin()) throw new CustomException("Unauthorized");
			
			
			if(!userDb.containsKey(userEmail)) throw new CustomException("Invalid Request");
			User user = userDb.get(userEmail);
			
			update(user, newName, newEmail, newAddress, newPhoneNumber);
		}
	}
	
	private void updatePassword(User user, String oldPassword, String newPassword) throws CustomException
	{
		user.changePassword(oldPassword, newPassword);
		
	}
	
	public void updateUserPassword(String email, String oldPassword, String newPassword) throws CustomException
	{
		if(currentUser.getEmail() == email) updatePassword(currentUser, oldPassword, newPassword);
		else throw new CustomException("Unauthorized");
		
		System.out.println("Changed Password");
		
	}
	
	
	
	
	
	
	

}
