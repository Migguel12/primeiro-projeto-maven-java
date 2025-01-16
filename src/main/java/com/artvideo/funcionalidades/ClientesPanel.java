package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        // Painel para busca de clientes
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Buscar");

        searchPanel.add(new JLabel("Buscar por Nome:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

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

        // Ação do botão "Buscar"
        searchButton.addActionListener(e -> {
            String nomePesquisa = searchField.getText();
            buscarClientes(nomePesquisa);
        });

        // Carregar clientes do banco de dados ao iniciar o painel
        loadClientes();
    }

    private void loadClientes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id_cliente, nome, email, telefone, endereco, data_cadastro FROM clientes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String endereco = rs.getString("endereco");
                String dataCadastro = rs.getString("data_cadastro");

                tableModel.addRow(new Object[]{idCliente, nome, email, telefone, endereco, dataCadastro});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarClientes(String nomePesquisa) {
        // Limpa a tabela antes de exibir os resultados da busca
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id_cliente, nome, email, telefone, endereco, data_cadastro FROM clientes WHERE nome LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nomePesquisa + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String endereco = rs.getString("endereco");
                String dataCadastro = rs.getString("data_cadastro");

                // Adiciona os dados à tabela
                tableModel.addRow(new Object[]{idCliente, nome, email, telefone, endereco, dataCadastro});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar clientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
