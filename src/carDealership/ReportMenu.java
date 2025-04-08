package carDealership;

import javax.swing.*;

import persistance.DBManager;

import java.awt.*;
import java.awt.event.*;

public class ReportMenu extends JDialog implements ActionListener {
    private JButton performanceButton;
    private JButton modelSalesButton;

    public ReportMenu(JDialog parent) {
        super(parent, "Report Options", true);

        setSize(300, 200);
        setLayout(new GridLayout(2, 1));
        setLocationRelativeTo(parent);

        performanceButton = new JButton("Salespeople Performance");
        performanceButton.addActionListener(this);
        add(performanceButton);

        modelSalesButton = new JButton("Model Sales");
        modelSalesButton.addActionListener(this);
        add(modelSalesButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == performanceButton) {
          JTextArea textArea = new JTextArea(DBManager.getSalesRepresentativeReport());
          textArea.setEditable(false);
          JScrollPane scrollPane = new JScrollPane(textArea);
          scrollPane.setPreferredSize(new Dimension(400, 300));
          JOptionPane.showMessageDialog(this, scrollPane, "Salesperson Performance", JOptionPane.PLAIN_MESSAGE);
        } else if (e.getSource() == modelSalesButton) {
          JTextArea textArea = new JTextArea(DBManager.getModelSalesReport());
          textArea.setEditable(false);
          JScrollPane scrollPane = new JScrollPane(textArea);
          scrollPane.setPreferredSize(new Dimension(400, 300));
          JOptionPane.showMessageDialog(this, scrollPane, "Model Sales", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
