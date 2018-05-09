package application;

import java.util.function.Supplier;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group());
		stage.setTitle("Swagger Generator");
		stage.setWidth(450);
		stage.setHeight(550);


		NamedCheckbox<Person> table = new NamedCheckbox<Person>(new Supplier<Person>() {

			@Override
			public Person get() {
				return new Person("NewPerson", false);
			}
		}, "PersonTable","Person", "IsMale");


		ObservableList<Node> children = ((Group) scene.getRoot()).getChildren();
		children.add(table);
		stage.setScene(scene);
		stage.show();
	}

	public class Person implements CheckboxTableModel {

		String name;
		Boolean checked;

		public Person(String name, Boolean checked) {
			super();
			this.name = name;
			this.checked = checked;
		}
		@Override
		public String getName() {
			return name;		
		}
		@Override
		public void setName(String name) {
			this.name = name;			
		}
		@Override
		public boolean isChecked() {
			return checked;		
		}
		@Override
		public void setChecked(boolean b) {
			this.checked = b;			
		}
	}

}