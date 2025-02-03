package com.artvideo.funcionalidades;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.artvideo.services.DatabaseConnection;

public class EditarClausulasFrame extends JFrame {
    private JTextArea clausulasTextArea;
    private JButton salvarButton;

    public EditarClausulasFrame() {
        setTitle("Editar Cláusulas do Contrato");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        clausulasTextArea = new JTextArea();
        add(new JScrollPane(clausulasTextArea), BorderLayout.CENTER);

        salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarClausulas();
            }
        });
        add(salvarButton, BorderLayout.SOUTH);

        carregarClausulas();
        setVisible(true);
    }

    private void carregarClausulas() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT texto_clausula FROM clausulas_contrato LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                clausulasTextArea.setText(rs.getString("texto_clausula"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cláusulas: " + e.getMessage());
        }
    }

    private void salvarClausulas() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("UPDATE clausulas_contrato SET texto_clausula = ? WHERE id_clausula = 1")) {
            stmt.setString(1, clausulasTextArea.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cláusulas atualizadas com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cláusulas: " + e.getMessage());
        }
    }
}
