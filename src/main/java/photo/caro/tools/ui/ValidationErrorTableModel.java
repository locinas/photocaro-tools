package photo.caro.tools.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import photo.caro.tools.business.globalvalidation.ValidationError;

public class ValidationErrorTableModel extends AbstractTableModel {

	private List<ValidationError> errors = new ArrayList<>();

	public ValidationErrorTableModel(List<ValidationError> errors) {
		super();
		if (errors != null) {
			this.errors.addAll(errors);
		}
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return errors.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (errors.isEmpty()) {
			return null;
		} else {
			return errors.get(row);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return ValidationError.class;
	}
	
	public List<ValidationError> getErrors() {
		return errors;
	}
}
