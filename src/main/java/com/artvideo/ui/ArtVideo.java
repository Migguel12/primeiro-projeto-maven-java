package com.artvideo.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.artvideo.funcionalidades.AgendaPanel;
import com.artvideo.funcionalidades.ClientesPanel; // Importando ClientePanel para a aba de Clientes
import com.artvideo.funcionalidades.ContratoPanel;

public class ArtVideo {
    public static void createWindow() {
        JFrame frame = new JFrame("Art Video");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Adicionando os painéis de cada aba
        mainPanel.add(new AgendaPanel(), "Agenda");
        mainPanel.add(new ClientesPanel(), "Clientes"); // Usando ClientePanel na aba Clientes
        mainPanel.add(new ContratoPanel(), "Contratos"); // Usando ContratoPanel na aba Contratos
        mainPanel.add(createSimplePanel("Receitas"), "Receitas");
        mainPanel.add(createSimplePanel("Despesas"), "Despesas");

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(60, 63, 65));
        sidebar.setPreferredSize(new Dimension(150, 0));

        sidebar.add(createSidebarButton("Agenda", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Clientes", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Contratos", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Receitas", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Despesas", cardLayout, mainPanel));

        frame.setLayout(new BorderLayout());
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JPanel createSimplePanel(String text) {
        JPanel panel = new JPanel();
        panel.add(new javax.swing.JLabel(text));
        return panel;
    }

    private static JButton createSidebarButton(String name, CardLayout layout, JPanel panel) {
        JButton button = new JButton(name);
        button.addActionListener(e -> layout.show(panel, name));
        return button;
    }
}
