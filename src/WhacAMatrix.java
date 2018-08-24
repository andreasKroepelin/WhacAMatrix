/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.util.prefs.Preferences;

/**
 *
 * @author kroepelin
 */
public class WhacAMatrix {

    public static Preferences prefs;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        prefs = Preferences.userRoot().node("pref");
        new Window();
    }

}
