package thatkow.fx.table.checkbox;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * @author andrewkowalczyk
 *
 * @param <T>
 * 
 * @code
 * 
 *       <pre>
 * 
 *       NamedCheckbox<Person> table = new NamedCheckbox<Person>(new Supplier<Person>() {
 * 
 *       	&#64;Override
 *       	public Person get() {
 *       		return new Person("NewPerson", false);
 *       	}
 *       });
 *       ((Group) scene.getRoot()).getChildren().add(table);
 *       </pre>
 */
public class NamedCheckbox<T extends NamedCheckboxTableModel> extends VBox {

	private final HBox hb = new HBox();
	private final TableView<EntityWrapper> table = new TableView<>();
	private final Label label;
	private final TableColumn nameColumn;
	private final TableColumn checkedColumn;
	private final Button addButton;
	private final Button deleteButton;
	private final Collection<T> collection;

	private class EntityWrapper {
		private SimpleStringProperty name = new SimpleStringProperty();
		private SimpleBooleanProperty set = new SimpleBooleanProperty();

		private T t;

		public EntityWrapper(T t) {
			super();
			this.name.set(t.getName());
			this.set.set(t.isChecked());
		}

		public String getName() {
			return name.get();
		}

		public void setName(String name) {
			t.setName(name);
			this.name.set(name);
		}

		public Boolean isSet() {
			return set.getValue();
		}

		public void setSet(Boolean set) {
			t.setChecked(set);
			this.set.set(set);
		}

		public T getT() {
			return t;
		}

	}

	private class EditingCell extends TableCell<T, String> {

		private TextField textField;

		public EditingCell() {
		}

		@Override
		public void startEdit() {
			if (!isEmpty()) {
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText((String) getItem());
			setGraphic(null);
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					if (!arg2) {
						commitEdit(textField.getText());
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}

	public NamedCheckbox(Collection<T> collection, Supplier<T> supplier) {

		this.collection = collection;

		label = new Label("");
		label.setFont(new Font("Arial", 20));

		table.setEditable(true);
		Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn p) {
				return new EditingCell();
			}
		};

		nameColumn = new TableColumn("Name");
		nameColumn.setMinWidth(100);
		nameColumn.setCellValueFactory(new PropertyValueFactory<T, String>(""));
		nameColumn.setCellFactory(cellFactory);
		nameColumn
				.setCellValueFactory(new Callback<CellDataFeatures<EntityWrapper, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<EntityWrapper, String> param) {
						EntityWrapper t = param.getValue();
						SimpleStringProperty prop = new SimpleStringProperty(t.getName());
						prop.addListener(new ChangeListener<String>() {

							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue,
									String newValue) {
								t.setName(newValue);
							}
						});
						return prop;
					}
				});

		checkedColumn = new TableColumn<>("Checked");
		checkedColumn.setCellValueFactory(
				new Callback<CellDataFeatures<EntityWrapper, Boolean>, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(CellDataFeatures<EntityWrapper, Boolean> param) {
						EntityWrapper t = param.getValue();
						SimpleBooleanProperty prop = new SimpleBooleanProperty(t.isSet());
						prop.addListener(new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								t.setSet(newValue);
							}
						});
						return prop;
					}
				});

		checkedColumn.setCellFactory(new Callback<TableColumn<T, Boolean>, //
				TableCell<T, Boolean>>() {
			@Override
			public TableCell<T, Boolean> call(TableColumn<T, Boolean> p) {
				CheckBoxTableCell<T, Boolean> cell = new CheckBoxTableCell<T, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});

		deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			int index = table.getSelectionModel().getSelectedIndex();
			collection.remove(index);
			reloadTableItems();

		});

		reloadTableItems();
		table.getColumns().addAll(nameColumn, checkedColumn);

		addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				collection.add(supplier.get());
				reloadTableItems();
			}
		});

		hb.getChildren().addAll(addButton, deleteButton);
		hb.setSpacing(3);

		this.setSpacing(5);
		this.setPadding(new Insets(10, 0, 0, 10));
		this.getChildren().addAll(label, table, hb);

	}

	public void reloadTableItems() {
		table.setItems(FXCollections
				.observableArrayList(collection.stream().map(e -> new EntityWrapper(e)).collect(Collectors.toList())));
	}

	public void setTitle(String str) {
		this.label.setText(str);
	}

	public void setName(String str) {
		this.nameColumn.setText(str);
	}

	public void setChecked(String str) {
		this.checkedColumn.setText(str);
	}

	public void setAdd(String str) {
		this.addButton.setText(str);
	}

	public void setDelete(String str) {
		this.deleteButton.setText(str);
	}

}