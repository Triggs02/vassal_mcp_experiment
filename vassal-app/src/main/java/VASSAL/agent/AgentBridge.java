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

import VASSAL.build.GameModule;
import VASSAL.configure.BooleanConfigurer;
import VASSAL.i18n.Resources;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

/**
 * Entry point for AI-player support. The bridge is the single place where an
 * external AI agent will be able to observe the game and act on it.
 *
 * The bridge is always constructed with the module but does nothing unless
 * the player switches it on in the preferences. For now it provides that
 * switch and a toolbar button whose menu reports the status and shows the
 * game as the AI sees it; later stages hang events and actions off this class.
 */
public class AgentBridge {
  /** Key of the per-module preference that switches AI-player support on. */
  public static final String ENABLED = "aiPlayerEnabled"; //NON-NLS

  private BooleanConfigurer enabledConfig;
  private JButton button;
  private AgentStateWindow stateWindow;

  /**
   * Registers the AI Player preference and toolbar button with the module.
   * @param mod the module being built
   */
  public void addTo(GameModule mod) {
    enabledConfig = new BooleanConfigurer(
      ENABLED, Resources.getString("AgentBridge.enable"), Boolean.FALSE //NON-NLS
    );
    mod.getPrefs().addOption(Resources.getString("AgentBridge.tab"), enabledConfig); //NON-NLS

    final JPopupMenu menu = new JPopupMenu();
    menu.add(Resources.getString("AgentBridge.menu_status")).addActionListener(e -> showStatus()); //NON-NLS
    menu.add(Resources.getString("AgentBridge.menu_show_state")).addActionListener(e -> showState()); //NON-NLS

    final Action menuAction = new AbstractAction(Resources.getString("AgentBridge.button")) { //NON-NLS
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        menu.show(button, 0, button.getHeight());
      }
    };

    button = mod.getToolBar().add(menuAction);
    button.setFocusable(false); // must not steal keystrokes from the map
    button.setToolTipText(Resources.getString("AgentBridge.button_tooltip")); //NON-NLS
    button.setAlignmentY((float) 0.0);
    button.setVisible(isEnabled());

    enabledConfig.addPropertyChangeListener(e -> button.setVisible(isEnabled()));
  }

  /** @return true if the player has switched AI-player support on */
  public boolean isEnabled() {
    return enabledConfig != null && Boolean.TRUE.equals(enabledConfig.getValue());
  }

  /** Reports the current state of the bridge in the chat window. */
  protected void showStatus() {
    GameModule.getGameModule().warn(Resources.getString("AgentBridge.status_not_connected")); //NON-NLS
  }

  /** Opens the "Show what the AI sees" window, reading the game afresh each time. */
  protected void showState() {
    if (stateWindow == null) {
      stateWindow = new AgentStateWindow(GameModule.getGameModule().getPlayerWindow());
    }
    stateWindow.refresh();
    stateWindow.setVisible(true);
    stateWindow.toFront();
  }
}
