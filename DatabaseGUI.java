package DbGUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class DatabaseGUI extends JFrame {
    private JTextArea queryArea;
    private JTable resultTable;
    private JTextField filterField;
    private JButton submitButton, filterButton;
    private JScrollPane tableScrollPane;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydbfall2024";
    private static final String USER = "root";
    private static final String PASS = "K@ustubh0912";

    public DatabaseGUI() {
        setTitle("Display Query Results");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel queryPanel = new JPanel(new BorderLayout());
        queryArea = new JTextArea("SELECT * FROM employee", 3, 20);
        queryArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        submitButton = new JButton("Submit Query");
        queryPanel.add(queryArea, BorderLayout.CENTER);
        queryPanel.add(submitButton, BorderLayout.EAST);
        add(queryPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        resultTable = new JTable(model);
        sorter = new TableRowSorter<>(model);
        resultTable.setRowSorter(sorter);
        tableScrollPane = new JScrollPane(resultTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterField = new JTextField(20);
        filterButton = new JButton("Apply Filter");
        filterPanel.add(new JLabel("Enter filter text:"));
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> executeQuery());
        filterButton.addActionListener(e -> applyFilter());
    }

    private void executeQuery() {
        String query = queryArea.getText();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            model.setColumnIdentifiers(columnNames);
            model.setRowCount(0);  

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            sorter.setRowFilter(null);
            filterField.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String text = filterField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DatabaseGUI().setVisible(true));
    }
}