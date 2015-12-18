package com.partnet.automation.util;

import javax.swing.JOptionPane;

/**
 * @author bbarker@part.net
 * @since 12/17/15
 */
public class Dialog {

  /**
   * Shows a dialog that blocks the current thread until the dialog is accepted.
   * This is useful when debugging tests
   * @param title - title of the dialog
   * @param message - message of the dialog
   */
  public static void showDialog(String title, String message) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
  }
}
