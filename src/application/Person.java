package application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
public class Person {
	
	private final SimpleStringProperty fName = new SimpleStringProperty("");
	private final SimpleBooleanProperty isVar = new SimpleBooleanProperty();

	Person(String name, boolean isVar){
		this.fName.set(name);;
		this.isVar.set(isVar);
	}


	public void setName(String name) {
		this.fName.set(name);
	}

	String getName() {
		return this.fName.get();
	}

	public void set(boolean b) {
		this.isVar.set(b);
	}

	public boolean get() {
		return this.isVar.get();
	}

}