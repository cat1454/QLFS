package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import utils.AuthUtils;
import AdminFunction.EmployeeManagementView;
import AdminFunction.StatisticsPanel;
import AdminFunction.RoleManagementView;
import OrderView.FlowerMenuView;

/**
 * Improved AdminView with professional UI design and better code structure
 */
public class AdminView extends JFrame {
    
    // Constants for consistent styling
    private static class UIConstants {
        static final Color PRIMARY_COLOR = new Color(52, 73, 94);
        static final Color SECONDARY_COLOR = new Color(52, 152, 219);
        static final Color SUCCESS_COLOR = new Color(46, 204, 113);
        static final Color DANGER_COLOR = new Color(231, 76, 60);
        static final Color LIGHT_BG = new Color(248, 249, 250);
        static final Color DARK_TEXT = new Color(33, 37, 41);
        static final Color BORDER_COLOR = new Color(220, 221, 225);
        
        static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
        static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);
        static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
        
        static final Dimension WINDOW_SIZE = new Dimension(1200, 800);
        static final Dimension MENU_SIZE = new Dimension(280, 0);
        static final int MENU_ITEM_HEIGHT = 50;
    }
    
    // UI Components
    private JLabel lblWelcome;
    private JLabel lblCurrentPage;
    private JPanel contentPanel;
    private JPanel menuPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> viewCache;
    private Map<String, Supplier<JPanel>> viewSuppliers;
    private String currentView = "Dashboard";
    
    // Views
    private FlowerMenuView flowerMenuView;
    private EmployeeManagementView employeeManagementView;
    private RoleManagementView roleManagementView;
    private StatisticsPanel statisticsPanel;
    
    public AdminView() {
        try {
            validateUserAccess();
            initializeComponents();
            setupViewSuppliers();
            setupKeyboardShortcuts();
            setLocationRelativeTo(null);
            
            // Show welcome message
            showWelcomeMessage();
            
        } catch (SecurityException e) {
            handleSecurityError(e);
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }
    
    private void validateUserAccess() throws SecurityException {
        if (!AuthUtils.isAdmin()) {
            throw new SecurityException("Không có quyền truy cập vào giao diện quản trị");
        }
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Flower Shop Management System - Admin Panel");
        setPreferredSize(UIConstants.WINDOW_SIZE);
        setMinimumSize(new Dimension(800, 600));
        
        // Initialize cache
        viewCache = new HashMap<>();
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Create components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        
        pack();
        
        // Load default view
        
        showView("Dashboard");
        
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel logo = new JLabel("🌸");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel title = new JLabel("FLOWER SHOP ADMIN");
        title.setFont(UIConstants.HEADER_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        leftPanel.add(logo);
        leftPanel.add(title);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        lblWelcome = new JLabel();
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(UIConstants.LABEL_FONT);
        
        JButton logoutBtn = createStyledButton("Đăng xuất", UIConstants.DANGER_COLOR, Color.BLACK);
        logoutBtn.addActionListener(e -> confirmLogout());
        
        rightPanel.add(lblWelcome);
        rightPanel.add(logoutBtn);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        // Update welcome text
        updateWelcomeText();
        
        return header;
    }
    
    private JPanel createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UIConstants.LIGHT_BG);
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER_COLOR));
        menuPanel.setPreferredSize(UIConstants.MENU_SIZE);
        
        // Menu title
        JLabel menuTitle = new JLabel("MENU ĐIỀU KHIỂN");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(UIConstants.DARK_TEXT.brighter());
        menuTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        menuPanel.add(menuTitle);
        
        // Menu items
        addMenuItem("📊", "Trang chủ", "Dashboard", () -> {
            showView("Dashboard");
          if (statisticsPanel != null) { statisticsPanel.refreshData();}
          else  statisticsPanel = new StatisticsPanel();
        });
        
        addMenuItem("👥", "Quản lý người dùng", "UserManagement", () -> {
            showView("UserManagement");
            if (employeeManagementView != null) employeeManagementView.loadUsersData();
        });
        
        addMenuItem("🌸", "Quản lý sản phẩm", "ProductManagement", () -> {
            showView("ProductManagement");
        });
        
        addMenuItem("🔐", "Quản lý phân quyền", "RoleManagement", () -> {
            showView("RoleManagement");
            if (roleManagementView != null) roleManagementView.loadUserData();
        });
        
        // Add spacer
        menuPanel.add(Box.createVerticalGlue());
        
        return menuPanel;
    }
    
    private void addMenuItem(String icon, String text, String viewName, Runnable action) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.MENU_ITEM_HEIGHT));
        menuItem.setPreferredSize(new Dimension(UIConstants.MENU_SIZE.width, UIConstants.MENU_ITEM_HEIGHT));
        menuItem.setBackground(UIConstants.LIGHT_BG);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        menuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 30));
        
        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(UIConstants.MENU_FONT);
        textLabel.setForeground(UIConstants.DARK_TEXT);
        
        menuItem.add(iconLabel, BorderLayout.WEST);
        menuItem.add(textLabel, BorderLayout.CENTER);
        
        // Hover effects
        addHoverEffect(menuItem, viewName);
        
        // Click handler
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        
        menuPanel.add(menuItem);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
    }
    
    private void addHoverEffect(JPanel menuItem, String viewName) {
        Color originalBg = menuItem.getBackground();
        Color hoverBg = UIConstants.SECONDARY_COLOR.brighter().brighter();
        Color activeBg = UIConstants.SECONDARY_COLOR.brighter();
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!currentView.equals(viewName)) {
                    menuItem.setBackground(hoverBg);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!currentView.equals(viewName)) {
                    menuItem.setBackground(originalBg);
                }
            }
        });
        
        // Update active state
        if (currentView.equals(viewName)) {
            menuItem.setBackground(activeBg);
        }
    }
    
    private JPanel createContentPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        
        // Breadcrumb
        JPanel breadcrumb = createBreadcrumb();
        mainContent.add(breadcrumb, BorderLayout.NORTH);
        
        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add loading panel
        contentPanel.add(createLoadingPanel(), "LOADING");
        
        mainContent.add(contentPanel, BorderLayout.CENTER);
        
        return mainContent;
    }
    
    private JPanel createBreadcrumb() {
        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT));
        breadcrumb.setBackground(Color.WHITE);
        breadcrumb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        JLabel home = new JLabel("Trang chủ");
        home.setFont(UIConstants.LABEL_FONT);
        home.setForeground(UIConstants.SECONDARY_COLOR);
        
        JLabel separator = new JLabel(" / ");
        separator.setFont(UIConstants.LABEL_FONT);
        separator.setForeground(UIConstants.DARK_TEXT.brighter());
        
        lblCurrentPage = new JLabel("Dashboard");
        lblCurrentPage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCurrentPage.setForeground(UIConstants.DARK_TEXT);
        
        breadcrumb.add(home);
        breadcrumb.add(separator);
        breadcrumb.add(lblCurrentPage);
        
        return breadcrumb;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UIConstants.LIGHT_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        JLabel status = new JLabel("Sẵn sàng");
        status.setFont(UIConstants.LABEL_FONT);
        status.setForeground(UIConstants.DARK_TEXT.brighter());
        
        JLabel time = new JLabel();
        time.setFont(UIConstants.LABEL_FONT);
        time.setForeground(UIConstants.DARK_TEXT.brighter());
        
        // Update time every second
        Timer timer = new Timer(00, e -> time.setText(new java.util.Date().toString()));
        timer.start();
        
        statusBar.add(status, BorderLayout.WEST);
        statusBar.add(time, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JPanel createLoadingPanel() {
        JPanel loading = new JPanel(new GridBagLayout());
        loading.setBackground(Color.WHITE);
        
        JLabel spinner = new JLabel("⏳");
        spinner.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        JLabel text = new JLabel("Đang tải...");
        text.setFont(UIConstants.MENU_FONT);
        text.setForeground(UIConstants.DARK_TEXT.brighter());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        loading.add(spinner, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        loading.add(text, gbc);
        
        return loading;
    }
    
    private void setupViewSuppliers() {
        this.viewSuppliers = Map.of(
            "Dashboard", () -> {
                if (statisticsPanel == null) {
                    statisticsPanel = new StatisticsPanel();
                }
                return statisticsPanel;
            },
            "UserManagement", () -> {
                if (employeeManagementView == null) {
                    employeeManagementView = new EmployeeManagementView();
                }
                return employeeManagementView;
            },
            "ProductManagement", () -> {
                if (flowerMenuView == null) {
                    flowerMenuView = new FlowerMenuView(false);
                }
                return flowerMenuView;
            },
            "RoleManagement", () -> {
                if (roleManagementView == null) {
                    roleManagementView = new RoleManagementView();
                }
                return roleManagementView;
            }
        );
    }
    
    private void showView(String viewName) {
        currentView = viewName;
        updateBreadcrumb(viewName);
        updateMenuSelection();
        
        // Show loading first
        cardLayout.show(contentPanel, "LOADING");
        
        // Load view in background
        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                return getOrCreateView(viewName);
            }
            
            @Override
            protected void done() {
                try {
                    
                    JPanel view = get();
                    if (!contentPanel.getLayout().equals(cardLayout)) return;
                    
                    // Add view if not exists
                    if (viewCache.get(viewName) == null) {
                        contentPanel.add(view, viewName);
                        viewCache.put(viewName, view);
                    }
                    
                    cardLayout.show(contentPanel, viewName);
                } catch (Exception e) {
                    
                    showErrorPanel("Lỗi tải giao diện: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private JPanel getOrCreateView(String viewName) {
        JPanel view = viewCache.get(viewName);
        if (view == null) {
            view = viewSuppliers.get(viewName).get();
        }
        return view;
    }
    
    private void updateBreadcrumb(String viewName) {
        String pageName = switch (viewName) {
            case "Dashboard" -> "Trang chủ";
            case "UserManagement" -> "Quản lý người dùng";
            case "ProductManagement" -> "Quản lý sản phẩm";
            case "RoleManagement" -> "Quản lý phân quyền";
            default -> "Không xác định";
        };
        lblCurrentPage.setText(pageName);
    }
    
    private void updateMenuSelection() {
        // Refresh menu to update active states
        Component[] components = menuPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.repaint();
            }
        }
    }
    
    private void showErrorPanel(String message) {
        JPanel errorPanel = new JPanel(new GridBagLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorIcon = new JLabel("❌");
        errorIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        JLabel errorText = new JLabel("<html><center>" + message + "</center></html>");
        errorText.setFont(UIConstants.MENU_FONT);
        errorText.setForeground(UIConstants.DANGER_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        errorPanel.add(errorIcon, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        errorPanel.add(errorText, gbc);
        
        contentPanel.add(errorPanel, "ERROR");
        cardLayout.show(contentPanel, "ERROR");
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupKeyboardShortcuts() {
        // Alt + 1: Dashboard
        this.getRootPane().registerKeyboardAction(
            e -> showView("Dashboard"),
            KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Alt + 2: User Management
        this.getRootPane().registerKeyboardAction(
            e -> showView("UserManagement"),
            KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Alt + 3: Product Management
        this.getRootPane().registerKeyboardAction(
            e -> showView("ProductManagement"),
            KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Alt + 4: Role Management
        this.getRootPane().registerKeyboardAction(
            e -> showView("RoleManagement"),
            KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Ctrl + Q: Logout
        this.getRootPane().registerKeyboardAction(
            e -> confirmLogout(),
            KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void updateWelcomeText() {
        if (LoginView.currentUserName != null && LoginView.currentUserRole != null) {
            lblWelcome.setText("Xin chào, " + LoginView.currentUserName + " (" + LoginView.currentUserRole + ")");
        }
    }
    
    private void showWelcomeMessage() {
        Timer timer = new Timer(1000, e -> {
            JOptionPane.showMessageDialog(this,
                "Chào mừng đến với hệ thống quản lý Flower Shop!\n\n" +
                "Sử dụng phím tắt:\n" +
                "• Alt + 1-4: Chuyển đổi giữa các trang\n" +
                "• Ctrl + Q: Đăng xuất",
                "Chào mừng",
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void confirmLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            performLogout();
        }
    }
    
    private void performLogout() {
        // Clear user data
        LoginView.currentUserName = null;
        LoginView.currentUserRole = null;
        
        // Cleanup resources
        cleanup();
        
        // Close current window and show login
        dispose();
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
    
    private void cleanup() {
        // Stop any running timers or background tasks
        if (flowerMenuView != null) {
            // flowerMenuView.cleanup(); // if exists
        }
        if (employeeManagementView != null) {
            // employeeManagementView.cleanup(); // if exists
        }
        if (roleManagementView != null) {
            // roleManagementView.cleanup(); // if exists
        }
        if (statisticsPanel != null) {
            // statisticsPanel.cleanup(); // if exists
        }
        
        // Clear cache
        viewCache.clear();
    }
    
    private void handleSecurityError(SecurityException e) {
        JOptionPane.showMessageDialog(null,
            e.getMessage(),
            "Lỗi phân quyền",
            JOptionPane.ERROR_MESSAGE);
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
    
    private void handleInitializationError(Exception e) {
        JOptionPane.showMessageDialog(null,
            "Lỗi khởi tạo giao diện: " + e.getMessage(),
            "Lỗi hệ thống",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        System.exit(1);
    }
    
    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }
}