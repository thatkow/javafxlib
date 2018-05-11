package thatkow.fx.table.checkbox;

public interface NamedCheckboxTableModel {

	public String getName();

	public void setName(String name);

	public boolean isChecked();

	public void setChecked(boolean checked);

	default String toString() {
		return getName() + "(checked=" + Boolean.toString(isChecked()) + ")";
	}

}
