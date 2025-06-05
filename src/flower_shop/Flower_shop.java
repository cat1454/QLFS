/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package flower_shop;

import AdminFunction.RoleManagementView;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.LoginView;




/**
 *
 * @author PHU
 */
public class Flower_shop {

    public static void main(String args[]) {
    
try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
    java.util.logging.Logger.getLogger(RoleManagementView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
}
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              
               new LoginView().setVisible(true);
            }
        });
    }
    
}
