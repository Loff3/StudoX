package View;

import Controller.ControllerInterface;
import Model.Dao.HistoryDao.CommandRecord;
import ObserverPattern.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel implements Observer {

    private ControllerInterface controller;

    // Components
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JButton revertButton;

    public HistoryPanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);
        initComponents();
        loadHistoryData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // History Table
        String[] historyColumns = {"Timestamp", "Command Description"};
        historyTableModel = new DefaultTableModel(historyColumns, 0) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        add(historyScrollPane, BorderLayout.CENTER);

        // Revert Button
        revertButton = new JButton("Revert to Selected Version");
        revertButton.setEnabled(false);
        revertButton.addActionListener(e -> revertToSelectedVersion());

        // Add ListSelectionListener to enable/disable the revert button
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && historyTable.getSelectedRow() >= 0) {
                revertButton.setEnabled(true);
            } else {
                revertButton.setEnabled(false);
            }
        });

        // Add revert button to panel
        JPanel revertPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        revertPanel.add(revertButton);
        add(revertPanel, BorderLayout.SOUTH);
    }

    private void revertToSelectedVersion() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                controller.revertToVersion(selectedRow);
                JOptionPane.showMessageDialog(this, "Reverted to the selected version successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reverting changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a version to revert to.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadHistoryData() {
        // Clear existing data
        historyTableModel.setRowCount(0);

        // Get command history from the controller
        List<CommandRecord> history = controller.getCommandHistory();

        // Add history entries to the table model
        for (CommandRecord record : history) {
            historyTableModel.addRow(new Object[]{
                    record.getTimestamp(),
                    record.getDescription()
            });
        }
    }

    @Override
    public void update(String message) {
        loadHistoryData();
    }
}
