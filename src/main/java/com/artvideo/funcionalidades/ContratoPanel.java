package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.artvideo.services.DatabaseConnection;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ContratoPanel extends JPanel {

    private final JComboBox<String> clienteComboBox;
    private final JTextField pesquisaField;
    private final JButton gerarContratoButton;
    private final JButton editarClausulasButton;
    private final Map<String, Integer> clienteMap; // Map para associar o nome do cliente ao ID
    private final DefaultListModel<String> contratoListModel; // Modelo para a lista de contratos
    private final JList<String> contratoList; // Lista para exibir os contratos gerados

    public ContratoPanel() {
        setLayout(new BorderLayout());

        clienteMap = new HashMap<>(); // Inicializa o mapa de clientes
        contratoListModel = new DefaultListModel<>();
        contratoList = new JList<>(contratoListModel);

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

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

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

        // Adiciona o botão de gerar contrato ao painel
        // add(gerarContratoButton, BorderLayout.SOUTH);

        // Botão de editar as clausulas
        editarClausulasButton = new JButton("Editar Cláusulas");
        editarClausulasButton.addActionListener(e -> new EditarClausulasFrame());
        
        botoesPanel.add(gerarContratoButton);
        botoesPanel.add(editarClausulasButton);

        add(botoesPanel, BorderLayout.SOUTH);

        // Seção para exibir a lista de contratos
        JPanel contratoPanel = new JPanel(new BorderLayout());
        contratoPanel.add(new JLabel("Contratos Gerados:"), BorderLayout.NORTH);
        contratoPanel.add(new JScrollPane(contratoList), BorderLayout.CENTER);
        add(contratoPanel, BorderLayout.EAST);

        // Chamar a função que configura a exibição e interação com os contratos gerados
        mostrarContratosGerados();
    }

    private void mostrarContratosGerados() {
        // Carregar contratos anteriores da pasta ou banco de dados
        carregarContratosAnteriores();

        contratoList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Clique duplo
                    int index = contratoList.locationToIndex(evt.getPoint()); // Obter índice do item clicado
                    if (index >= 0) {
                        String contratoSelecionado = contratoListModel.get(index); // Obter item selecionado
                        String[] partes = contratoSelecionado.split(" - ");
                        if (partes.length == 2) {
                            String fileName = partes[1]; // Nome do arquivo PDF
                            abrirPDF(fileName); // Abrir PDF
                        } else {
                            JOptionPane.showMessageDialog(ContratoPanel.this, "Erro ao localizar o arquivo.");
                        }
                    }
                }
            }
        });
    }

    private void carregarContratosAnteriores() {
        // Verificar se os contratos estão armazenados no banco de dados
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT nome_cliente, nome_arquivo FROM contratos")) {

            while (resultSet.next()) {
                String clienteNome = resultSet.getString("nome_cliente");
                String arquivoContrato = resultSet.getString("nome_arquivo");
                contratoListModel.addElement(clienteNome + " - " + arquivoContrato);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contratos anteriores: " + e.getMessage());
        }
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

    private String carregarClausulasDoBanco() {
        StringBuilder clausulas = new StringBuilder();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT texto_clausula FROM clausulas_contrato");
             ResultSet resultSet = statement.executeQuery()) {
    
            while (resultSet.next()) {
                clausulas.append(resultSet.getString("texto_clausula")).append("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cláusulas: " + e.getMessage());
        }
        return clausulas.toString();
    }
    

    private void gerarContratoPDF(String cliente) {
        try {
            // Obter o ID do cliente a partir do mapa
            Integer clienteId = clienteMap.get(cliente);

            // Texto base do contrato
            String clausulas = carregarClausulasDoBanco();

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

            // Adicionar contrato gerado à lista
            contratoListModel.addElement(cliente + " - " + fileName);

            // Salvar no banco de dados
            salvarContratoNoBanco(cliente, fileName);

            // Abrir arquivo PDF
            abrirPDF(fileName);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar contrato: " + ex.getMessage());
        }
    }

    private void salvarContratoNoBanco(String cliente, String fileName) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "INSERT INTO contratos (id_cliente, nome_cliente, nome_arquivo) VALUES (?, ?, ?)")) {

        Integer clienteId = clienteMap.get(cliente);
        if (clienteId == null) {
            JOptionPane.showMessageDialog(this, "ID do cliente não encontrado para o cliente: " + cliente);
            return;
        }

        preparedStatement.setInt(1, clienteId);
        preparedStatement.setString(2, cliente);
        preparedStatement.setString(3, fileName);

        preparedStatement.executeUpdate();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erro ao salvar contrato no banco: " + e.getMessage());
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
