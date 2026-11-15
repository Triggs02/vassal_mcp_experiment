/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.agent;

import VASSAL.i18n.Resources;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The "Show what the AI sees" window: the text from
 * {@link GameStateDescriber}, with a button to read the game again.
 */
public class AgentStateWindow extends JDialog {
  private static final long serialVersionUID = 1L;

  private final JTextArea text = new JTextArea(30, 80);

  public AgentStateWindow(Frame owner) {
    super(owner, Resources.getString("AgentBridge.state_window_title"), false); //NON-NLS

    text.setEditable(false);
    text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, text.getFont().getSize()));

    final JButton refresh = new JButton(Resources.getString("General.refresh")); //NON-NLS
    refresh.addActionListener(e -> refresh());

    final JButton close = new JButton(Resources.getString("General.close")); //NON-NLS
    close.addActionListener(e -> setVisible(false));

    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(refresh);
    buttons.add(close);

    setLayout(new BorderLayout());
    add(new JScrollPane(text), BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(owner);
  }

  /** Reads the game again and shows the result from the top. */
  public void refresh() {
    text.setText(GameStateDescriber.describe());
    text.setCaretPosition(0);
  }
}
