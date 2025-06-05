package view;

import OrderView.CartView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import OrderView.FlowerMenuView;
import OrderView.OrderHistoryView;
import UserFunction.UserProfileView;
import utils.AuthUtils;

public class UserView extends JFrame {
    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(255, 87, 34);
    private static final Color SIDEBAR_COLOR = new Color(250, 250, 250);
    private static final Color HOVER_COLOR = new Color(230, 230, 230);
    private static final Color ACTIVE_COLOR = new Color(63, 81, 181, 50);
    
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font WELCOME_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private static final Dimension WINDOW_SIZE = new Dimension(1200, 800);
    private static final Dimension SIDEBAR_SIZE = new Dimension(280, 600);
    
    // UI Components
    private JLabel lblWelcome;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;
    
    // Content panels
    private JPanel homePanel;
    private FlowerMenuView flowerMenuView;
    private UserProfileView profileView;
    private OrderHistoryView orderHistoryView;
    private CartView cartView;
    
    // Menu buttons
    private JButton[] menuButtons;
    private String[] cardNames = {"Home", "Order", "Cart", "Profile", "History"};
    // Option 1: Simple text icons (always work)
    private String[] buttonTexts = {"⌂  Trang chính", "♦  Đặt hàng", "◉  Giỏ hàng", 
                                   "●  Thông tin cá nhân", "≡  Lịch sử đơn hàng"};
    
    // Option 2: Unicode symbols (better compatibility)
    // private String[] buttonTexts = {"▣  Trang chính", "⚡  Đặt hàng", "◈  Giỏ hàng", 
    //                                "◐  Thông tin cá nhân", "☰  Lịch sử đơn hàng"};
    
    private int activeButtonIndex = 0;

    public UserView() {
        initializeUI();
        setupEventHandlers();
        setLocationRelativeTo(null);
        
        // Set welcome message
        lblWelcome.setText("Xin chào, " + LoginView.currentUserName + " (" + LoginView.currentUserRole + ")");
        
        // Show home by default
        showCard("Home", 0);
    }

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Flower Shop - Hệ thống đặt hoa");
        setPreferredSize(WINDOW_SIZE);
        setMinimumSize(new Dimension(900, 600));
        
     
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        
        // Create components
        JPanel headerPanel = createHeaderPanel();
        sidebarPanel = createSidebarPanel();
        contentPanel = createContentPanel();
        
        // Layout
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(sidebarPanel, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer);
        pack();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("❀");
        logoLabel.setFont(new Font("Serif", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Flower Shop");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        lblWelcome = new JLabel();
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(WELCOME_FONT);
        
        JButton btnLogout = createStyledButton("Đăng xuất", ACCENT_COLOR, Color.BLACK);
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(lblWelcome);
        rightPanel.add(btnLogout);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(SIDEBAR_SIZE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        
        // Add padding at top
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Create menu buttons
        menuButtons = new JButton[buttonTexts.length];
        
        for (int i = 0; i < buttonTexts.length; i++) {
            menuButtons[i] = createMenuButton(buttonTexts[i], i);
            sidebar.add(menuButtons[i]);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Add flexible space at bottom
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text, int index) {
        JButton button = new JButton(text);
        button.setFont(MENU_FONT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(250, 50));
        button.setBackground(SIDEBAR_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover and click effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeButtonIndex != index) {
                    button.setBackground(HOVER_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (activeButtonIndex != index) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(ACTIVE_COLOR);
            }
        });
        
        return button;
    }
    
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        JPanel content = new JPanel(cardLayout);
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Initialize content panels
        homePanel = createHomePanel();
        flowerMenuView = new FlowerMenuView(false);
        cartView = new CartView();
        profileView = new UserProfileView(AuthUtils.getLoggedInUserId());
        orderHistoryView = new OrderHistoryView(AuthUtils.getLoggedInUserId());
        
        // Add cards
        content.add(homePanel, "Home");
        content.add(flowerMenuView, "Order");
        content.add(cartView, "Cart");
        content.add(profileView, "Profile");
        content.add(orderHistoryView, "History");
        
        return content;
    }
    
    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBackground(Color.WHITE);
        
        // Welcome section
        JPanel welcomeSection = new JPanel(new BorderLayout());
        welcomeSection.setBackground(Color.WHITE);
        welcomeSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel welcomeTitle = new JLabel("Chào mừng đến với Flower Shop! ❀");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(PRIMARY_COLOR);
        
        JLabel welcomeText = new JLabel("<html>" +
                "<div style='color: #666; font-size: 14px; line-height: 1.6;'>" +
                "Khám phá bộ sưu tập hoa tươi đẹp của chúng tôi.<br>" +
                "Đặt hàng dễ dàng và nhận hoa tại nhà với chất lượng tốt nhất!" +
                "</div></html>");
        
        welcomeSection.add(welcomeTitle, BorderLayout.NORTH);
        welcomeSection.add(welcomeText, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = createQuickActionsPanel();
        
        home.add(welcomeSection, BorderLayout.NORTH);
        home.add(actionsPanel, BorderLayout.CENTER);
        
        return home;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel actions = new JPanel(new GridLayout(2, 2, 20, 20));
        actions.setBackground(Color.WHITE);
        actions.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Thao tác nhanh",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR
        ));
        
        // Quick action cards with simple icons
        JPanel orderCard = createActionCard("♦", "Đặt hàng ngay", "Xem các loại hoa có sẵn", 1);
        JPanel cartCard = createActionCard("◉", "Xem giỏ hàng", "Kiểm tra đơn hàng của bạn", 2);
        JPanel profileCard = createActionCard("●", "Cập nhật thông tin", "Chỉnh sửa thông tin cá nhân", 3);
        JPanel historyCard = createActionCard("≡", "Lịch sử đơn hàng", "Xem các đơn hàng đã đặt", 4);
        
        actions.add(orderCard);
        actions.add(cartCard);
        actions.add(profileCard);
        actions.add(historyCard);
        
        return actions;
    }
    
    private JPanel createActionCard(String icon, String title, String description, int cardIndex) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        iconLabel.setForeground(PRIMARY_COLOR);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center; color: #666;'>" + description + "</div></html>");
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.CENTER);
        textPanel.add(descLabel, BorderLayout.SOUTH);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.SOUTH);
        
        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                showCard(cardNames[cardIndex], cardIndex);
            }
        });
        
        return card;
    }
    
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Menu button actions
        for (int i = 0; i < menuButtons.length; i++) {
            final int index = i;
            final String cardName = cardNames[i];
            
            menuButtons[i].addActionListener(e -> {
                showCard(cardName, index);
                if ("Order".equals(cardName)) {
                    flowerMenuView.reloadFlowers();
                }
                // Special actions for certain cards
                if ("Cart".equals(cardName)) {
                    cartView.refreshCart();
                } else if ("History".equals(cardName)) {
                    orderHistoryView.loadOrders();
                }
            });
        }
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        
        // Ctrl+1 to Ctrl+5 for menu navigation
        for (int i = 0; i < 5; i++) {
            final int index = i;
            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl " + (i + 1)), "menu" + i);
            rootPane.getActionMap().put("menu" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showCard(cardNames[index], index);
                }
            });
        }
        
        // Ctrl+Q for logout
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ctrl Q"), "logout");
        rootPane.getActionMap().put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }
    
    private void showCard(String cardName, int buttonIndex) {
        cardLayout.show(contentPanel, cardName);
        updateActiveButton(buttonIndex);
    }
    
    private void updateActiveButton(int newActiveIndex) {
        // Reset previous active button
        if (activeButtonIndex >= 0 && activeButtonIndex < menuButtons.length) {
            menuButtons[activeButtonIndex].setBackground(SIDEBAR_COLOR);
            menuButtons[activeButtonIndex].setForeground(Color.BLACK);
        }
        
        // Set new active button
        activeButtonIndex = newActiveIndex;
        if (activeButtonIndex >= 0 && activeButtonIndex < menuButtons.length) {
            menuButtons[activeButtonIndex].setBackground(ACTIVE_COLOR);
            menuButtons[activeButtonIndex].setForeground(PRIMARY_COLOR);
        }
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            LoginView.currentUserName = null;
            LoginView.currentUserRole = null;
            this.dispose();
            new LoginView().setVisible(true);
        }
    }
}