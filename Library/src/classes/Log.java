package classes;

import java.time.LocalDate;

public class Log {

	private final String logId;
	private static int logIdCounter = 1;
	private String userId;
	private LocalDate in;
	private LocalDate out;
	
	public Log(String userId)
	{
		this.userId = userId;
		logId = "l"+(logIdCounter++);
		setIn(LocalDate.now());
	}

	public String getLogId() {
		return logId;
	}

	public String getUserId() {
		return userId;
	}

	public LocalDate getIn() {
		return in;
	}

	private void setIn(LocalDate in) {
		this.in = in;
	}

	public LocalDate getOut() {
		return out;
	}

	public void setOut(LocalDate out) {
		this.out = out;
	}
	
	
	
}
