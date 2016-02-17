package photo.caro.tools.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import photo.caro.tools.business.globalvalidation.SeverityErrorEnum;
import photo.caro.tools.business.globalvalidation.ValidationError;

public class ValidationErrorCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		ValidationError validationError = (ValidationError)value;
		
		JPanel validationErrorPanel = new JPanel();
		validationErrorPanel.setLayout(new BoxLayout(validationErrorPanel, BoxLayout.X_AXIS));
		validationErrorPanel.setBackground(Color.WHITE);
		validationErrorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		if(validationError != null) {
			if(SeverityErrorEnum.ERREUR.equals(validationError.getSeverity())) {
				validationErrorPanel.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("error-icon.png"))));
			} else if(SeverityErrorEnum.WARNNING.equals(validationError.getSeverity())) {
				validationErrorPanel.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("warning-icon.png"))));
			}
			JLabel messageLabel = new JLabel("  "+validationError.getMessage());
			validationErrorPanel.add(messageLabel);
		}

		return validationErrorPanel;
	}

}
