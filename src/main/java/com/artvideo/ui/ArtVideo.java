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
import com.artvideo.funcionalidades.ClientesPanel;
import com.artvideo.funcionalidades.ContratoPanel;
import com.artvideo.funcionalidades.FinanceiroPanel;

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
        mainPanel.add(new ClientesPanel(), "Clientes");
        mainPanel.add(new ContratoPanel(), "Contratos");
        mainPanel.add(new FinanceiroPanel(), "Finanças");

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(60, 63, 65));
        sidebar.setPreferredSize(new Dimension(150, 0));

        sidebar.add(createSidebarButton("Agenda", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Clientes", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Contratos", cardLayout, mainPanel));
        sidebar.add(createSidebarButton("Finanças", cardLayout, mainPanel));

        frame.setLayout(new BorderLayout());
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JButton createSidebarButton(String name, CardLayout layout, JPanel panel) {
        JButton button = new JButton(name);
        button.addActionListener(e -> layout.show(panel, name));
        return button;
    }
}
