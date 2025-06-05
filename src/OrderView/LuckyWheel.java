package OrderView;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Interface callback để nhận kết quả quay
interface SpinResultCallback {
    void onSpinComplete(String prizeLabel);
    void onAllSpinsCompleted(String finalPrizeLabel);
}

public class LuckyWheel extends JFrame implements ActionListener {
    
    // Constants
    private static final int WHEEL_SIZE = 400;
    private static final int ANIMATION_DURATION = 4000;
    private static final int EXTRA_ROTATIONS = 5;
    private static final int TIMER_DELAY = 8;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    private static final int MAX_SPINS = 3; // Số lần quay tối đa
    
    private String selectedPrizeLabel;
    private SpinResultCallback callback;
    private int currentSpinCount = 0; // Đếm số lần đã quay
    private boolean gameCompleted = false; // Trạng thái hoàn thành game
    
    static class Prize {
        String label;
        int weight;
        Color color;

        public Prize(String label, int weight, Color color) {
            this.label = label;
            this.weight = weight;
            this.color = color;
        }
    }

    private final Prize[] prizes = {
        new Prize("0%", 40, null),
        new Prize("5%", 20, null),
        new Prize("10%", 15, null),
        new Prize("15%", 10, null),
        new Prize("20%", 5, null),
        new Prize("30%", 4, null),
        new Prize("40%", 3, null),
        new Prize("50%", 1, null)
    };
    
    // Pool of beautiful colors for random selection
    private final Color[] colorPool = {
        new Color(255, 107, 107),    // Soft Red
        new Color(255, 159, 67),     // Orange  
        new Color(255, 206, 84),     // Yellow
        new Color(123, 237, 159),    // Green
        new Color(112, 161, 255),    // Blue
        new Color(196, 181, 253),    // Purple
        new Color(255, 154, 158),    // Pink
        new Color(102, 217, 232),    // Cyan
        new Color(254, 202, 87),     // Gold
        new Color(165, 243, 252),    // Light Cyan
        new Color(251, 191, 36),     // Amber
        new Color(167, 243, 208),    // Emerald
        new Color(147, 197, 253),    // Light Blue
        new Color(196, 181, 253),    // Lavender
        new Color(252, 165, 165),    // Rose
        new Color(134, 239, 172),    // Lime
        new Color(251, 146, 60),     // Orange Red
        new Color(139, 92, 246),     // Violet
        new Color(34, 197, 94),      // Green 500
        new Color(239, 68, 68)       // Red 500
    };

    private Timer timer;
    private double currentAngle = 0;
    private double targetAngle = 0;
    private long startTime;
    private boolean spinning = false;
    private int resultIndex;
    
    // UI Components
    private JButton spinButton;
    private JButton proceedButton; // Nút tiến hành đặt hàng
    private JLabel titleLabel;
    private JLabel resultLabel;
    private JLabel spinCountLabel; // Hiển thị số lần quay
    private JPanel mainPanel;
    private JPanel wheelPanel;
    private JPanel actionPanel; // FIX: Tạo reference trực tiếp
    private JPanel buttonPanel; // FIX: Tạo reference trực tiếp
    
    // Animation effects
    private float pulseScale = 1.0f;
    private int sparkleOffset = 0;
    private Random random = new Random();

    // Constructor với callback
    public LuckyWheel(SpinResultCallback callback) {
        this.callback = callback;
        initializeLuckyWheel();
    }
    
    // Constructor không callback (tương thích với code cũ)
    public LuckyWheel() {
        this(null);
    }
    
    private void initializeLuckyWheel() {
        setTitle("VÒNG QUAY MAY MẮN");
        setSize(700, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        assignRandomColors();
        initializeUI();
        timer = new Timer(TIMER_DELAY, this);
    }
    
    private void assignRandomColors() {
        // Create shuffled list of colors
        java.util.List<Color> availableColors = new java.util.ArrayList<>(Arrays.asList(colorPool));
        java.util.Collections.shuffle(availableColors, random);
        
        // Assign unique colors to each prize
        for (int i = 0; i < prizes.length; i++) {
            prizes[i].color = availableColors.get(i % availableColors.size());
        }
        
       
    }
    
    public void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(26, 32, 44)); // Dark background
        
        // Main panel with gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(26, 32, 44),
                    0, getHeight(), new Color(45, 55, 72)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Sparkle effects
                drawSparkles(g2d);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Title
        titleLabel = new JLabel(" VÒNG QUAY MAY MẮN ", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // Spin count label
        spinCountLabel = new JLabel("Lượt quay: " + currentSpinCount + "/" + MAX_SPINS, SwingConstants.CENTER);
        spinCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        spinCountLabel.setForeground(new Color(255, 206, 84));
        spinCountLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(spinCountLabel, BorderLayout.SOUTH);
        
        // Wheel panel
        wheelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawWheel(g);
            }
        };
        wheelPanel.setPreferredSize(new Dimension(WHEEL_SIZE + 100, WHEEL_SIZE + 100));
        wheelPanel.setOpaque(false);
        
        // Result label
        resultLabel = new JLabel("Chúc bạn may mắn! ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setForeground(new Color(255, 206, 84));
        resultLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // Buttons
        spinButton = createSpinButton();
        proceedButton = createProceedButton();
     
        // Button panel - FIX: Tạo reference trực tiếp
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(spinButton);
        
        // Add color randomize button
        JButton randomizeButton = createRandomizeButton();
        buttonPanel.add(randomizeButton);
        
        // Action panel - FIX: Tạo reference trực tiếp và initially hidden
        actionPanel = new JPanel(new FlowLayout());
        actionPanel.setOpaque(false);
        actionPanel.add(proceedButton);
        actionPanel.setVisible(false);
        
        JPanel allButtonsPanel = new JPanel(new BorderLayout());
        allButtonsPanel.setOpaque(false);
        allButtonsPanel.add(buttonPanel, BorderLayout.NORTH);
        allButtonsPanel.add(actionPanel, BorderLayout.SOUTH);
        allButtonsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Assembly
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(wheelPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(resultLabel, BorderLayout.NORTH);
        bottomPanel.add(allButtonsPanel, BorderLayout.CENTER);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(600, 750));
    }

    private JButton createSpinButton() {
        JButton button = new JButton("QUAY NGAY ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background with gradient
                if (getModel().isPressed()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 107, 107), 
                                                 0, getHeight(), new Color(255, 159, 67)));
                } else if (getModel().isRollover()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 159, 67), 
                                                 0, getHeight(), new Color(255, 206, 84)));
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 206, 84), 
                                                 0, getHeight(), new Color(255, 159, 67)));
                }
                
                // Scale effect
                if (spinning) {
                    int offset = (int)((1 - pulseScale) * getWidth() / 2);
                    g2d.fillRoundRect(offset, offset, 
                        (int)(getWidth() * pulseScale), (int)(getHeight() * pulseScale), 25, 25);
                } else {
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                }
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 60));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> startSpin());
        return button;
    }
    
 
    private JButton createProceedButton() {
        JButton button = new JButton("ĐẶT HÀNG NGAY") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(34, 197, 94), 
                                                 0, getHeight(), new Color(22, 163, 74)));
                } else if (getModel().isRollover()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(22, 163, 74), 
                                                 0, getHeight(), new Color(21, 128, 61)));
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(21, 128, 61), 
                                                 0, getHeight(), new Color(20, 83, 45)));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(140, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> proceedToOrder());
        return button;
    }
    
    private JButton createRandomizeButton() {
        JButton button = new JButton(" ĐỔI MÀU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(147, 197, 253), 
                                                 0, getHeight(), new Color(59, 130, 246)));
                } else if (getModel().isRollover()) {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(59, 130, 246), 
                                                 0, getHeight(), new Color(37, 99, 235)));
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(37, 99, 235), 
                                                 0, getHeight(), new Color(29, 78, 216)));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            if (!spinning && !gameCompleted) {
                assignRandomColors();
                wheelPanel.repaint();
                resultLabel.setText("Màu sắc đã được thay đổi!");
                resultLabel.setForeground(new Color(147, 197, 253));
            }
        });
        
        return button;
    }

    private void drawSparkles(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 20; i++) {
            int x = (sparkleOffset + i * 50) % getWidth();
            int y = (i * 37) % getHeight();
            int size = 2 + (i % 3);
            g2d.fillOval(x, y, size, size);
        }
        sparkleOffset = (sparkleOffset + 1) % getWidth();
    }

    private void drawWheel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int centerX = wheelPanel.getWidth() / 2;
        int centerY = wheelPanel.getHeight() / 2;
        int radius = WHEEL_SIZE / 2;

        g2d.translate(centerX, centerY);

        // Outer glow effect
        for (int i = 10; i > 0; i--) {
            g2d.setColor(new Color(255, 255, 255, 5));
            g2d.fillOval(-radius - i, -radius - i, (radius + i) * 2, (radius + i) * 2);
        }

        double anglePerSegment = 360.0 / prizes.length;

        // Draw segments
        for (int i = 0; i < prizes.length; i++) {
            double startAngle = currentAngle + i * anglePerSegment;
            
            Color segmentColor = prizes[i].color;
            
            // Highlight winning segment
            if (!spinning && i == resultIndex) {
                segmentColor = segmentColor.brighter();
                pulseScale = 1.0f + 0.1f * (float)Math.sin(System.currentTimeMillis() * 0.01);
            }
            
            g2d.setColor(segmentColor);
            
            Shape arc = new Arc2D.Double(-radius, -radius, radius * 2, radius * 2,
                    -startAngle, -anglePerSegment, Arc2D.PIE);
            g2d.fill(arc);

            // Segment border
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(arc);

            // Draw labels
            drawSegmentLabel(g2d, prizes[i].label, startAngle + anglePerSegment / 2, radius - 60);
        }

        // Center circle
        int centerSize = 40;
        g2d.setPaint(new RadialGradientPaint(0, 0, centerSize/2, 
            new float[]{0, 1}, new Color[]{Color.WHITE, new Color(200, 200, 200)}));
        g2d.fillOval(-centerSize/2, -centerSize/2, centerSize, centerSize);
        
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(-centerSize/2, -centerSize/2, centerSize, centerSize);

        // Arrow pointer
        drawArrow(g2d, radius);

        g2d.dispose();
    }

    private void drawSegmentLabel(Graphics2D g2d, String text, double angle, int distance) {
        double theta = Math.toRadians(angle);
        int x = (int) (Math.cos(theta) * distance);
        int y = (int) (Math.sin(theta) * distance);

        g2d.setColor(Color.WHITE);
        g2d.setFont(LABEL_FONT);
        
        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, x - fm.stringWidth(text) / 2 + 1, y + 1);
        
        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    private void drawArrow(Graphics2D g2d, int radius) {
        // Arrow shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        Polygon shadowArrow = new Polygon();
        shadowArrow.addPoint(2, -radius + 12);
        shadowArrow.addPoint(-13, -radius - 3);
        shadowArrow.addPoint(17, -radius - 3);
        g2d.fill(shadowArrow);
        
        // Main arrow
        g2d.setPaint(new GradientPaint(0, -radius, Color.WHITE, 0, -radius + 20, new Color(200, 200, 200)));
        Polygon arrow = new Polygon();
        arrow.addPoint(0, -radius + 10);
        arrow.addPoint(-15, -radius - 5);
        arrow.addPoint(15, -radius - 5);
        g2d.fill(arrow);
        
        // Arrow border
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(arrow);
    }

    public void startSpin() {
        if (spinning || gameCompleted) return;
        
        spinning = true;
        spinButton.setEnabled(false);
        spinButton.setText("ĐANG QUAY... ️");
        resultLabel.setText("Đang quay... ");
        resultLabel.setForeground(new Color(255, 159, 67));

        // Select random prize
        resultIndex = getWeightedRandomPrizeIndex();
        double anglePerSegment = 360.0 / prizes.length;

        // Calculate target angle
        double desiredAngle = 270 - (resultIndex * anglePerSegment + anglePerSegment / 2);
        if (desiredAngle < 0) desiredAngle += 360;

        targetAngle = 360 * EXTRA_ROTATIONS + desiredAngle;
        currentAngle = 0;
        startTime = System.currentTimeMillis();

        timer.start();
    }

    private int getWeightedRandomPrizeIndex() {
        int totalWeight = Arrays.stream(prizes).mapToInt(p -> p.weight).sum();
        int rand = random.nextInt(totalWeight);
        int cumulative = 0;
        
        for (int i = 0; i < prizes.length; i++) {
            cumulative += prizes[i].weight;
            if (rand < cumulative) return i;
        }
        return prizes.length - 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long elapsed = System.currentTimeMillis() - startTime;
        mainPanel.repaint(); // For sparkle animation

        if (elapsed >= ANIMATION_DURATION) {
            // Animation complete
            currentAngle = targetAngle;
            spinning = false;
            timer.stop();
            
            // Increase spin count
            currentSpinCount++;
            spinCountLabel.setText("Lượt quay: " + currentSpinCount + "/" + MAX_SPINS);
            
            // Show result
            String prize = prizes[resultIndex].label;
            selectedPrizeLabel = prize; // FIX: Đảm bảo gán giá trị cho selectedPrizeLabel
            
            if (prize.equals("0%")) {
                resultLabel.setText("Chúc bạn may mắn lần sau! ");
                resultLabel.setForeground(new Color(255, 107, 107));
            } else {
                resultLabel.setText("🎉 Chúc mừng! Bạn nhận được " + prize + " giảm giá!");
                resultLabel.setForeground(new Color(123, 237, 159));
            }
            
            System.out.println("Phần thưởng: " + prize);
            wheelPanel.repaint();
            
            // Show result dialog and handle next steps
            Timer delayTimer = new Timer(500, evt -> {
                showResultDialog(prize);
                ((Timer)evt.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
            
            return;
        }

        // Smooth easing animation
        double progress = (double) elapsed / ANIMATION_DURATION;
        double easedProgress = 1 - Math.pow(1 - progress, 3); // Ease out cubic
        currentAngle = targetAngle * easedProgress;

        wheelPanel.repaint();
    }

    private void showResultDialog(String prize) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   "Kết quả lần " + currentSpinCount, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(26, 32, 44));
        
        JLabel messageLabel = new JLabel("<html><center>" +
            (prize.equals("0%") ? 
                " Rất tiếc!<br>Bạn chưa trúng thưởng lần này" :
                " Chúc mừng!<br>Bạn đã nhận được mã giảm giá <b>" + prize + "</b>") +
            "<br><br>Lượt quay: " + currentSpinCount + "/" + MAX_SPINS +
            "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(new EmptyBorder(30, 30, 20, 30));
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.addActionListener(e -> {
            dialog.dispose();
            handleSpinComplete(prize);
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(26, 32, 44));
        buttonPanel.add(okButton);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void handleSpinComplete(String prize) {
        // Gọi callback cho mỗi lần quay
        if (callback != null) {
            callback.onSpinComplete(prize);
        }
        
        // Kiểm tra xem đã hết lượt quay chưa
        if (currentSpinCount >= MAX_SPINS) {
            // Hết lượt quay - kết thúc game
            gameCompleted = true;
            spinButton.setEnabled(false);
            spinButton.setText("HẾT LƯỢT QUAY");
            
            // Hiển thị nút đặt hàng
            showFinalButtons();
            
            // Gọi callback kết thúc game
            if (callback != null && callback instanceof SpinResultCallback) {
                callback.onAllSpinsCompleted(selectedPrizeLabel);
            }
        } else {
            // Còn lượt quay - cho phép quay tiếp hoặc dừng lại
            spinButton.setEnabled(true);
            spinButton.setText("QUAY LẠI (" + (MAX_SPINS - currentSpinCount) + " lượt còn lại)");
           
            // Hiển thị các nút lựa chọn
            showActionButtons();
        }
    }
    
    private void showActionButtons() {
       
// Hiển thị nút xác nhận và đặt hàng
        actionPanel.setVisible(true);
        resultLabel.setText(resultLabel.getText() + " - Bạn có muốn quay tiếp?");
        revalidate();
        repaint();
    }
    
    private void showFinalButtons() {
        // Ẩn nút quay và hiển thị nút đặt hàng
        spinButton.setVisible(false);
        actionPanel.setVisible(true);         
        resultLabel.setText(" Game kết thúc! Nhấn 'ĐẶT HÀNG NGAY' để tiếp tục.");
        resultLabel.setForeground(new Color(123, 237, 159));
        revalidate();
        repaint();
    }
    
 
  private void proceedToOrder() {
    // Đặt hàng ngay với phần thưởng vừa nhận
    String currentPrize = selectedPrizeLabel != null ? selectedPrizeLabel : "0%";
    
    int result = JOptionPane.showConfirmDialog(
        this,
        "Đặt hàng ngay với mã giảm giá: " + currentPrize + "?\n" +
        "(Bạn sẽ không được quay thêm lần nào)",
        "Xác nhận đặt hàng",
        JOptionPane.YES_NO_OPTION
    );
    
    if (result == JOptionPane.YES_OPTION) {
        if (callback != null) {
            callback.onAllSpinsCompleted(currentPrize);
        }
        dispose(); // Đóng cửa sổ
    }
}
    // Getter method (vẫn giữ cho tương thích)
    public String getSelectedPrizeLabel() {
        return selectedPrizeLabel;
    }
    
    // Method để set callback sau khi tạo object
    public void setSpinResultCallback(SpinResultCallback callback) {
        this.callback = callback;
    }
    
    // Method để reset game (nếu cần)
    public void resetGame() {
        currentSpinCount = 0;
        gameCompleted = false;
        selectedPrizeLabel = null;
        spinButton.setEnabled(true);
        spinButton.setText("QUAY NGAY ");
        spinButton.setVisible(true);
        spinCountLabel.setText("Lượt quay: 0/" + MAX_SPINS);
        resultLabel.setText("Chúc bạn may mắn! ");
        resultLabel.setForeground(new Color(255, 206, 84));
        
        // Ẩn action buttons
        Component[] components = ((JPanel) mainPanel.getComponent(2)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getComponentCount() == 2) { // Action panel
                    panel.setVisible(false);
                    panel.getComponent(0).setVisible(true); // Show confirm button again
                    break;
                }
            }
        }
        
        revalidate();
        repaint();
    }
}