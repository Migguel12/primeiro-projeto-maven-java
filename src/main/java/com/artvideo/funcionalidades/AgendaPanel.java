package com.artvideo.funcionalidades;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.artvideo.services.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

public class AgendaPanel extends JPanel {
    public AgendaPanel() {
        setLayout(new BorderLayout());
        
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);

        try {
            GoogleCalendarService googleCalendarService = new GoogleCalendarService();
            List<Event> events = googleCalendarService.getUpcomingEvents();
            StringBuilder eventsText = new StringBuilder();

            if (events != null && !events.isEmpty()) {
                for (Event event : events) {
                    eventsText.append("Event: ").append(event.getSummary()).append("\n");
                    eventsText.append("Start: ").append(event.getStart().getDateTime()).append("\n\n");
                }
            } else {
                eventsText.append("No upcoming events found.");
            }

            textArea.setText(eventsText.toString());
        } catch (Exception e) {
            textArea.setText("Failed to load events: " + e.getMessage());
        }

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}
