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

import VASSAL.Info;
import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.PlayerRoster;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.counters.Deck;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Properties;
import VASSAL.counters.Stack;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns the current game into plain text: the module, the players, and for
 * every map its boards, zones and pieces with the name of the location each
 * piece is in. This is the AI's view of the game.
 *
 * Everything here comes from what VASSAL knows about any loaded module;
 * nothing is specific to one game. Pieces are described as the local player
 * sees them: a masked piece reports the name its mask shows, a piece hidden
 * from the local player is left out, and the contents of decks are not
 * listed.
 *
 * Must be called on the Swing event thread, like all access to game state.
 */
public final class GameStateDescriber {
  /** Zone lists longer than this are cut short to keep the text readable. */
  private static final int MAX_ZONES_LISTED = 40;

  private static final String INDENT = "  "; //NON-NLS

  private GameStateDescriber() {
  }

  /** @return a plain-text description of the module and the game in progress */
  public static String describe() {
    final GameModule mod = GameModule.getGameModule();
    final StringBuilder sb = new StringBuilder();

    sb.append("Module: ").append(mod.getGameName()).append(' ').append(mod.getGameVersion()).append('\n'); //NON-NLS
    sb.append("VASSAL: ").append(Info.getVersion()).append('\n'); //NON-NLS
    sb.append("Game in progress: ").append(mod.getGameState().isGameStarted() ? "yes" : "no").append('\n'); //NON-NLS

    describePlayers(mod, sb);

    final List<Map> maps = Map.getMapList();
    sb.append('\n').append("Maps: ").append(maps.size()).append('\n'); //NON-NLS
    for (final Map map : maps) {
      describeMap(map, sb);
    }

    return sb.toString();
  }

  private static void describePlayers(GameModule mod, StringBuilder sb) {
    final Object name = mod.getPrefs().getValue(GameModule.REAL_NAME);
    final String mySide = PlayerRoster.getMySide();
    sb.append("You are: ").append(name == null ? "(no name)" : name) //NON-NLS
      .append(", side: ").append(mySide == null ? "(none)" : mySide).append('\n'); //NON-NLS

    final PlayerRoster roster = mod.getPlayerRoster();
    if (roster == null) {
      return;
    }

    sb.append("Sides: ").append(String.join(", ", roster.getSides())).append('\n'); //NON-NLS

    sb.append("Players:"); //NON-NLS
    final PlayerRoster.PlayerInfo[] players = roster.getPlayers();
    if (players.length == 0) {
      sb.append(" (none)"); //NON-NLS
    }
    sb.append('\n');
    for (final PlayerRoster.PlayerInfo p : players) {
      sb.append(INDENT).append(p.playerName).append(" - ").append(p.getSide()).append('\n'); //NON-NLS
    }
  }

  private static void describeMap(Map map, StringBuilder sb) {
    sb.append('\n').append("Map: ").append(map.getMapName()).append('\n'); //NON-NLS

    final List<String> boardNames = new ArrayList<>();
    final List<String> zoneNames = new ArrayList<>();
    for (final Board b : map.getBoards()) {
      boardNames.add(b.getName());
      for (final Zone z : b.getAllDescendantComponentsOf(Zone.class)) {
        zoneNames.add(z.getName());
      }
    }

    if (boardNames.isEmpty()) {
      sb.append(INDENT).append("Boards: (none set up yet)").append('\n'); //NON-NLS
    }
    else {
      final Dimension size = map.mapSize();
      sb.append(INDENT).append("Boards: ").append(String.join(", ", boardNames)) //NON-NLS
        .append(" (").append(size.width).append(" x ").append(size.height).append(" pixels)").append('\n'); //NON-NLS
    }

    if (!zoneNames.isEmpty()) {
      final int shown = Math.min(zoneNames.size(), MAX_ZONES_LISTED);
      sb.append(INDENT).append("Zones (").append(zoneNames.size()).append("): ") //NON-NLS
        .append(String.join(", ", zoneNames.subList(0, shown)));
      if (shown < zoneNames.size()) {
        sb.append(", ... and ").append(zoneNames.size() - shown).append(" more"); //NON-NLS
      }
      sb.append('\n');
    }

    final StringBuilder pieces = new StringBuilder();
    int count = 0;
    for (final GamePiece top : map.getAllPieces()) {
      if (top instanceof Deck) {
        final Deck deck = (Deck) top;
        pieces.append(INDENT).append(INDENT).append("Deck \"").append(deck.getDeckName()).append("\": ") //NON-NLS
          .append(deck.getPieceCount()).append(" pieces").append(where(map, deck.getPosition())).append('\n'); //NON-NLS
      }
      else if (top instanceof Stack) {
        final List<GamePiece> visible = new ArrayList<>();
        for (final GamePiece p : ((Stack) top).asList()) {
          if (isVisibleToMe(p)) {
            visible.add(p);
          }
        }
        if (visible.size() > 1) {
          pieces.append(INDENT).append(INDENT).append("Stack of ").append(visible.size()) //NON-NLS
            .append(where(map, top.getPosition())).append(", bottom to top:").append('\n'); //NON-NLS
          for (final GamePiece p : visible) {
            pieces.append(INDENT).append(INDENT).append(INDENT).append(nameOf(p)).append('\n');
          }
        }
        else if (visible.size() == 1) {
          appendPiece(map, visible.get(0), pieces);
        }
        count += visible.size();
      }
      else if (isVisibleToMe(top)) {
        appendPiece(map, top, pieces);
        count++;
      }
    }

    sb.append(INDENT).append("Pieces: ").append(count).append('\n').append(pieces); //NON-NLS
  }

  private static void appendPiece(Map map, GamePiece p, StringBuilder sb) {
    sb.append(INDENT).append(INDENT).append(nameOf(p)).append(where(map, p.getPosition())).append('\n');
  }

  private static boolean isVisibleToMe(GamePiece p) {
    return !Boolean.TRUE.equals(p.getProperty(Properties.INVISIBLE_TO_ME));
  }

  /** The piece's name as the local player sees it; a masked piece gives the name its mask shows. */
  private static String nameOf(GamePiece p) {
    final String name = p.getName();
    if (name == null || name.isBlank()) {
      return Boolean.TRUE.equals(p.getProperty(Properties.OBSCURED_TO_ME)) ? "(masked piece)" : "(unnamed piece)"; //NON-NLS
    }
    return name;
  }

  private static String where(Map map, Point pos) {
    final String loc = map.locationName(pos);
    final String xy = "(" + pos.x + ", " + pos.y + ")"; //NON-NLS
    return (loc == null || loc.isBlank()) ? " at " + xy : " at " + loc + " " + xy; //NON-NLS
  }
}
