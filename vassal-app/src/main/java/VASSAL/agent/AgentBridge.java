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

/**
 * Entry point for AI-player support. The bridge is the single place where an
 * external AI agent will be able to observe the game and act on it.
 *
 * The bridge is always constructed with the module but does nothing unless
 * the player switches it on in the preferences. For now it only provides
 * that switch and a toolbar button; later stages hang state export, events
 * and actions off this class.
 */
public class AgentBridge {
  /** Key of the per-module preference that switches AI-player support on. */
  public static final String ENABLED = "aiPlayerEnabled"; //NON-NLS

  private BooleanConfigurer enabledConfig;
  private JButton button;

  /**
   * Registers the AI Player preference and toolbar button with the module.
   * @param mod the module being built
   */
  public void addTo(GameModule mod) {
    enabledConfig = new BooleanConfigurer(
      ENABLED, Resources.getString("AgentBridge.enable"), Boolean.FALSE //NON-NLS
    );
    mod.getPrefs().addOption(Resources.getString("AgentBridge.tab"), enabledConfig); //NON-NLS

    final Action statusAction = new AbstractAction(Resources.getString("AgentBridge.button")) { //NON-NLS
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        showStatus();
      }
    };

    button = mod.getToolBar().add(statusAction);
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
}
