package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.artvideo.services.DatabaseConnection;

public class FinanceiroPanel extends JPanel {

    private JLabel totalReceitasLabel;
    private JLabel totalDespesasLabel;
    private JLabel saldoLabel;
    private JTable tabelaMovimentacoes;
    private DefaultTableModel tableModel;
    private JComboBox<String> filtroTipoComboBox;

    public FinanceiroPanel() {
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        totalReceitasLabel = new JLabel("Total a Receber: R$ 0.00");
        totalDespesasLabel = new JLabel("Total de Custos: R$ 0.00");
        saldoLabel = new JLabel("Saldo do Mês: R$ 0.00");

        totalReceitasLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalDespesasLabel.setFont(new Font("Arial", Font.BOLD, 14));
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        infoPanel.add(totalReceitasLabel);
        infoPanel.add(totalDespesasLabel);
        infoPanel.add(saldoLabel);

        add(infoPanel, BorderLayout.NORTH);

        // Configuração da tabela
        String[] colunas = {"ID", "Data", "Tipo", "Descrição", "Valor"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaMovimentacoes = new JTable(tableModel);

        add(new JScrollPane(tabelaMovimentacoes), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel();
        JButton btnAdicionarMovimentacao = new JButton("Adicionar Movimentação");
        btnAdicionarMovimentacao.addActionListener(e -> adicionarMovimentacao());
        botoesPanel.add(btnAdicionarMovimentacao);

        filtroTipoComboBox = new JComboBox<>(new String[]{"Todas", "Receita", "Despesa"});
        filtroTipoComboBox.addActionListener(e -> carregarMovimentacoes());
        botoesPanel.add(filtroTipoComboBox);

        add(botoesPanel, BorderLayout.SOUTH);

        atualizarFinanceiro();
        carregarMovimentacoes();
    }

    private void atualizarFinanceiro() {
        String sql = """
            SELECT 
                SUM(CASE WHEN tipo = 'Receita' THEN valor ELSE 0 END) AS total_receber, 
                SUM(CASE WHEN tipo = 'Despesa' THEN valor ELSE 0 END) AS total_custos
            FROM financas
            WHERE MONTH(data_registro) = MONTH(CURDATE()) 
            AND YEAR(data_registro) = YEAR(CURDATE());
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                double totalReceber = rs.getDouble("total_receber");
                double totalCustos = rs.getDouble("total_custos");
                double saldo = totalReceber - totalCustos;

                totalReceitasLabel.setText(String.format("Total a Receber: R$ %.2f", totalReceber));
                totalDespesasLabel.setText(String.format("Total de Custos: R$ %.2f", totalCustos));
                saldoLabel.setText(String.format("Saldo do Mês: R$ %.2f", saldo));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados financeiros: " + e.getMessage());
        }
    }

    private void carregarMovimentacoes() {
        String filtroTipo = (String) filtroTipoComboBox.getSelectedItem();
        String sql;
        boolean filtrar = !"Todas".equals(filtroTipo);

        if (filtrar) {
            sql = "SELECT id_financa, data_registro, tipo, descricao, valor FROM financas WHERE tipo = ? ORDER BY data_registro DESC";
        } else {
            sql = "SELECT id_financa, data_registro, tipo, descricao, valor FROM financas ORDER BY data_registro DESC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (filtrar) {
                stmt.setString(1, filtroTipo);
            }

            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_financa"),
                    rs.getDate("data_registro"),
                    rs.getString("tipo"),
                    rs.getString("descricao"),
                    rs.getDouble("valor")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar movimentações: " + e.getMessage());
        }
    }

    private void adicionarMovimentacao() {
        JTextField campoData = new JTextField();
        JComboBox<String> campoTipo = new JComboBox<>(new String[]{"Receita","Despesa"});
        JTextField campoDescricao = new JTextField();
        JTextField campoCategoria = new JTextField();
        JTextField campoValor = new JTextField();


        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Data (YYYY-MM-DD):"));
        panel.add(campoData);
        panel.add(new JLabel("Tipo (Receita/Despesa):"));
        panel.add(campoTipo);
        panel.add(new JLabel("Descrição:"));
        panel.add(campoDescricao);
        panel.add(new JLabel("Categoria:"));
        panel.add(campoCategoria);
        panel.add(new JLabel("Valor:"));
        panel.add(campoValor);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Movimentação", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String data = campoData.getText().trim();
            String tipo = (String) campoTipo.getSelectedItem();
            String descricao = campoDescricao.getText().trim();
            String categoria = campoCategoria.getText().trim();
            double valor;

            try {
                valor = Double.parseDouble(campoValor.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido.");
                return;
            }

            String sql = "INSERT INTO financas (data_registro, tipo, descricao, categoria, valor) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, data);
                stmt.setString(2, tipo);
                stmt.setString(3, descricao);
                stmt.setString(4, categoria);
                stmt.setDouble(5, valor);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Movimentação adicionada com sucesso!");
                atualizarFinanceiro();
                carregarMovimentacoes();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar movimentação: " + e.getMessage());
            }
        }
    }
}
