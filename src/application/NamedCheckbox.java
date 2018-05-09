package application;

import java.util.function.Supplier;

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

public class NamedCheckbox<T extends CheckboxTableModel> extends VBox {

	private final Supplier<T> supplier ;

	private final ObservableList<EntityWrapper> data =
			FXCollections.observableArrayList();
	private final HBox hb = new HBox();
	private TableView<EntityWrapper> table = new TableView<>();
	class EntityWrapper {
		private SimpleStringProperty name = new SimpleStringProperty();
		private SimpleBooleanProperty set = new SimpleBooleanProperty();
		public EntityWrapper(String name, Boolean set) {
			super();
			this.name.set(name);
			this.set.set(set);
		}
		public String getName() {
			return name.get();
		}
		public void setName(String name) {
			this.name.set(name);
		}
		public Boolean isSet() {
			return set.getValue();
		}
		public void setSet(Boolean set) {
			this.set.set(set);
		}


	}
	
	class EditingCell extends TableCell<T, String> {
		 
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
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, 
                    Boolean arg1, Boolean arg2) {
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

	public NamedCheckbox(Supplier<T> supplier, String title, String name, String checkName ) {

		this.supplier = supplier;

		final Label label = new Label(title);
		label.setFont(new Font("Arial", 20));


		table.setEditable(true);
		Callback<TableColumn, TableCell> cellFactory =
				new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn p) {
				return new EditingCell();
			}
		};

		TableColumn nameCol = new TableColumn(name);
		nameCol.setMinWidth(100);
		nameCol.setCellValueFactory(
				new PropertyValueFactory<T, String>(name));
		nameCol.setCellFactory(cellFactory);
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<EntityWrapper, String>, ObservableValue<String>>() {

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

		TableColumn singleCol = new TableColumn<>(checkName);
		singleCol.setCellValueFactory(new Callback<CellDataFeatures<EntityWrapper, Boolean>, ObservableValue<Boolean>>() {

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

		singleCol.setCellFactory(new Callback<TableColumn<T, Boolean>, //
				TableCell<T, Boolean>>() {
			@Override
			public TableCell<T, Boolean> call(TableColumn<T, Boolean> p) {
				CheckBoxTableCell<T, Boolean> cell = new CheckBoxTableCell<T, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});

		final Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			EntityWrapper selectedItem = table.getSelectionModel().getSelectedItem();
			table.getItems().remove(selectedItem);
		});

		table.setItems(data);
		table.getColumns().addAll(nameCol, singleCol);


		final Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				T t = supplier.get();
				data.add(new EntityWrapper(t.getName(), t.isChecked()));
			}
		});

		hb.getChildren().addAll(addButton,deleteButton);
		hb.setSpacing(3);

		
		this.setSpacing(5);
		this.setPadding(new Insets(10, 0, 0, 10));
		this.getChildren().addAll(label, table, hb);

	}
	
//	public void addToObservableList(ObservableList<Node> list) {
//		list.add(this);
//		list.add(vbox);
//	}


}