package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.artvideo.services.DatabaseConnection;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ContratoPanel extends JPanel {

    private final JComboBox<String> clienteComboBox;
    private final JTextField pesquisaField; // Adicionado campo de pesquisa
    private final JButton gerarContratoButton;
    private final Map<String, Integer> clienteMap; // Map para associar o nome do cliente ao ID

    public ContratoPanel() {
        setLayout(new BorderLayout());

        clienteMap = new HashMap<>(); // Inicializa o mapa de clientes

        // Título da aba
        JLabel titulo = new JLabel("Gerenciar Contratos", JLabel.CENTER);
        titulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Seção de seleção de cliente
        JPanel clientePanel = new JPanel();
        clientePanel.setLayout(new FlowLayout());
        clientePanel.add(new JLabel("Buscar Cliente:"));

        // Campo de texto para pesquisa
        pesquisaField = new JTextField(20);
        clientePanel.add(pesquisaField);

        // ComboBox de clientes
        clienteComboBox = new JComboBox<>();
        clientePanel.add(clienteComboBox);
        add(clientePanel, BorderLayout.CENTER);

        // Carregar clientes reais do banco de dados
        carregarClientes();

        // Atualizar a lista com base na barra de pesquisa
        pesquisaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarClientes();
            }
        });

        // Botão para gerar contrato
        gerarContratoButton = new JButton("Gerar Contrato");
        gerarContratoButton.addActionListener(e -> {
            String clienteSelecionado = (String) clienteComboBox.getSelectedItem();
            if (clienteSelecionado != null) {
                gerarContratoPDF(clienteSelecionado);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum cliente selecionado.");
            }
        });

        add(gerarContratoButton, BorderLayout.SOUTH);
    }

    private void carregarClientes() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id_cliente, nome FROM clientes")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_cliente");
                String nome = resultSet.getString("nome");
                clienteComboBox.addItem(nome); // Adiciona o nome ao ComboBox
                clienteMap.put(nome, id); // Mapeia o nome ao ID
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void filtrarClientes() {
        String textoPesquisa = pesquisaField.getText().toLowerCase();
        clienteComboBox.removeAllItems(); // Limpa o ComboBox antes de preencher novamente

        // Adiciona os nomes que contêm o texto digitado
        for (String nome : clienteMap.keySet()) {
            if (nome.toLowerCase().contains(textoPesquisa)) {
                clienteComboBox.addItem(nome);
            }
        }

        // Exibe mensagem se nenhum cliente for encontrado
        if (clienteComboBox.getItemCount() == 0) {
            clienteComboBox.addItem("Nenhum cliente encontrado");
        }
    }

    private void gerarContratoPDF(String cliente) {
        try {
            // Obter o ID do cliente a partir do mapa
            Integer clienteId = clienteMap.get(cliente);

            // Texto base do contrato
            String clausulas = "Contrato entre a empresa ArtVideo e o cliente " + cliente + ".\n" +
                    "Cláusulas: \n1. O cliente concorda com os termos...\n2. Prazo de validade: 12 meses.";

            // Nome do arquivo PDF
            String fileName = "Contrato_" + cliente.replace(" ", "_") + ".pdf";

            // Criar o documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Adicionar conteúdo ao PDF
            document.add(new Paragraph("Contrato ArtVideo"));
            document.add(new Paragraph("Cliente: " + cliente));
            document.add(new Paragraph("ID do Cliente: " + clienteId));
            document.add(new Paragraph("Data: " + new Date()));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(clausulas));

            document.close();

            JOptionPane.showMessageDialog(this, "Contrato gerado com sucesso: " + fileName);

            // Abrir arquivo PDF
            abrirPDF(fileName);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar contrato: " + ex.getMessage());
        }
    }

    private void abrirPDF(String fileName) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                java.io.File file = new java.io.File(fileName);
                if (file.exists()) {
                    desktop.open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "Arquivo não encontrado: " + fileName);
                }
            } else {
                JOptionPane.showMessageDialog(this, "A abertura automática de arquivos não é suportada no sistema atual.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o PDF: " + e.getMessage());
        }
    }
}
