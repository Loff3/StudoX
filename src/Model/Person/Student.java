package Model.Person;

public class Student {
	private String personID;
	private String name;
	private String personalNumber;
	private String email;
	private String phoneNumber;
	private String program;

	public Student(String personID, String name, String personalNumber, String email, String phoneNumber,
			String program) {
		this.personID = personID;
		this.name = name;
		this.personalNumber = personalNumber;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.program = program;
	}

	// Getters (no setters to maintain immutability)
	public String getPersonID() {
		return personID;
	}

	public String getName() {
		return name;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getProgram() {
		return program;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPersonalNumber(String number) {
		this.personalNumber = personalNumber;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public void setPhoneNumber(String number) {
		this.phoneNumber = phoneNumber;
	}

}
