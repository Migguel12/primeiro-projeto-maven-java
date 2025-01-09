package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.artvideo.services.DatabaseConnection;

public class ClientesPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ClientesPanel() {
        setLayout(new BorderLayout());

        // Criação do modelo da tabela
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Email", "Telefone", "Endereço", "Data Cadastro"}, 0);
        table = new JTable(tableModel);

        // Painel para exibir a tabela
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Painel para adicionar novos clientes
        JPanel addPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        addPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Cliente"));

        JTextField nomeField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField enderecoField = new JTextField();

        addPanel.add(new JLabel("Nome:"));
        addPanel.add(nomeField);
        addPanel.add(new JLabel("Email:"));
        addPanel.add(emailField);
        addPanel.add(new JLabel("Telefone:"));
        addPanel.add(telefoneField);
        addPanel.add(new JLabel("Endereço:"));
        addPanel.add(enderecoField);

        JButton addButton = new JButton("Adicionar");
        addPanel.add(new JLabel()); // Espaçamento
        addPanel.add(addButton);

        add(addPanel, BorderLayout.SOUTH);

        // Ação do botão "Adicionar"
        addButton.addActionListener(e -> {
            String nome = nomeField.getText();
            String email = emailField.getText();
            String telefone = telefoneField.getText();
            String endereco = enderecoField.getText();

            if (nome.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO clientes (nome, email, telefone, endereco) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, telefone);
                stmt.setString(4, endereco);

                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // Atualiza a tabela com o novo cliente
                    tableModel.addRow(new Object[]{null, nome, email, telefone, endereco, java.time.LocalDate.now()});

                    // Limpa os campos
                    nomeField.setText("");
                    emailField.setText("");
                    telefoneField.setText("");
                    enderecoField.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
