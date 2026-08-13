import javax.swing.*;

import WPILibUtils.MathUtil;
import WPILibUtils.PIDController;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EmojiShooter {
    private static final int GRID_WIDTH = 31;
    private static final int GRID_HEIGHT = 17;
    private static final int CELL_SIZE = 38;
    private static final int PANEL_WIDTH = GRID_WIDTH * CELL_SIZE;
    private static final int PANEL_HEIGHT = GRID_HEIGHT * CELL_SIZE + 80;
    private static final double MAX_ANGULAR_VELOCITY = 0.2;
    private static final double FRICTION = 0.90;
    private static final int TIMER_DELAY = 60;
    private static final List<String> TARGET_EMOJIS = Arrays.asList("👾", "🍎", "🎯", "💎", "🦄", "🍕", "🧠", "🐙");
    private static final String FIRE_EMOJI = "🔥";
    private static final String EXPLOSION_EMOJI = "💥";
    private static final String EYE_EMOJI = "👁";

    private final int eyeX = GRID_WIDTH / 2;
    private final int eyeY = GRID_HEIGHT - 1;
    private final Random random = new Random();

    private double angleRad = Math.PI / 2.0;
    private double angularVelocity = 0.0;
    private boolean firing = false;
    private int beamLength = 0;
    private int hitFrames = 0;
    private int explosionFrames = 0;
    private Target target;
    private boolean leftDown = false;
    private boolean rightDown = false;
    private boolean shootDown = false;
    private boolean shootEdge = false;

    private PIDController controller = new PIDController(0, 0, 0);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmojiShooter().createAndShowGui());
    }

    private void createAndShowGui() {
        target = createTarget();
        JFrame frame = new JFrame("Emoji Shooter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        panel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(true, e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKey(false, e);
            }
        });
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();

        Timer timer = new Timer(TIMER_DELAY, e -> {
            // ControlOutput control = controlOutput(leftDown, rightDown, shootEdge);
            ControlOutput control = autoControlOutput(target.x, GRID_HEIGHT - target.y, eyeX, GRID_HEIGHT - eyeY, angleRad, controller);
            shootEdge = false;
            updatePhysics(control);
            panel.repaint();
        });
        timer.start();
    }

    private void handleKey(boolean pressed, KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftDown = pressed;
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightDown = pressed;
        } else if (code == KeyEvent.VK_SPACE) {
            if (pressed) {
                shootEdge = true;
            }
            shootDown = pressed;
        } else if (code == KeyEvent.VK_Q) {
            System.exit(0);
        }
    }

    private void updatePhysics(ControlOutput control) {
        angularVelocity += control.angularAcceleration;
        if (control.angularAcceleration == 0.0) {
            angularVelocity *= FRICTION;
            if (Math.abs(angularVelocity) < 0.005) {
                angularVelocity = 0.0;
            }
        }
        angularVelocity = clamp(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        angleRad += angularVelocity;
        if (angleRad < 0.0) {
            angleRad = 0.0;
            if (angularVelocity < 0) {
                angularVelocity = 0.0;
            }
        }
        if (angleRad > Math.PI) {
            angleRad = Math.PI;
            if (angularVelocity > 0) {
                angularVelocity = 0.0;
            }
        }

        if (control.shooting && !firing && explosionFrames == 0) {
            firing = true;
            beamLength = 0;
            hitFrames = 0;
        }

        if (firing) {
            beamLength+=3;
            int maxRange = computeBeamRange();
            if (beamLength > maxRange) {
                firing = false;
            }
            if (hitFrames == 0 && isBeamNearTarget()) {
                hitFrames = 5;
                firing = false;
                explosionFrames = 8;
            }
        }

        if (hitFrames > 0) {
            hitFrames--;
        }
        if (explosionFrames > 0) {
            explosionFrames--;
            if (explosionFrames == 0) {
                target = createTarget();
            }
        }
    }

    private boolean isBeamNearTarget() {
        for (int step = 1; step <= beamLength; step++) {
            int x = eyeX + (int) Math.round(Math.cos(angleRad) * step);
            int y = eyeY - (int) Math.round(Math.sin(angleRad) * step);
            if (x == target.x && y == target.y) {
                return true;
            }
            double dx = target.x - x;
            double dy = target.y - y;
            if (dx * dx + dy * dy <= 1.4) {
                return true;
            }
        }
        return false;
    }

    private static ControlOutput controlOutput(boolean leftPressed, boolean rightPressed, boolean shootPressed) {
        double angularAcceleration = 0.0;
        boolean shoot = false;
        return new ControlOutput(angularAcceleration, shoot);
    }

    private static ControlOutput autoControlOutput(double targetX, double targetY, double eyeX, double eyeY, double currentAngleRad, PIDController controller) {
        double angularAcceleration = 0.0;
        boolean shoot = false;
        return new ControlOutput(angularAcceleration, shoot);
    }

    private Target createTarget() {
        int x, y;
        do {
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT - 4);
        } while ((x == eyeX && y == eyeY) || y >= eyeY - 1);
        String emoji = TARGET_EMOJIS.get(random.nextInt(TARGET_EMOJIS.size()));
        return new Target(x, y, emoji);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private int computeBeamRange() {
        int maxRange = 0;
        while (true) {
            int step = maxRange + 1;
            int x = eyeX + (int) Math.round(Math.cos(angleRad) * step);
            int y = eyeY - (int) Math.round(Math.sin(angleRad) * step);
            if (!isInside(x, y)) {
                break;
            }
            maxRange = step;
        }
        return maxRange;
    }

    private final class GamePanel extends JPanel {
        private final Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 28);
        private final Font statusFont = new Font("SansSerif", Font.PLAIN, 14);

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());

            drawGrid(g2);
            drawInactiveBeam(g2);
            if (firing || hitFrames > 0) {
                drawActiveBeam(g2);
            }
            if (explosionFrames > 0) {
                drawExplosion(g2);
            }
            drawTarget(g2);
            drawEye(g2);
            drawStatus(g2);
            g2.dispose();
        }

        private void drawGrid(Graphics2D g2) {
            g2.setColor(new Color(36, 36, 36));
            g2.fillRect(0, 0, GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
            g2.setColor(new Color(64, 64, 64));
            for (int x = 0; x <= GRID_WIDTH; x++) {
                g2.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
            }
            for (int y = 0; y <= GRID_HEIGHT; y++) {
                g2.drawLine(0, y * CELL_SIZE, GRID_WIDTH * CELL_SIZE, y * CELL_SIZE);
            }
        }

        private void drawTarget(Graphics2D g2) {
            String emoji = target.emoji;
            if (hitFrames > 0) {
                emoji = FIRE_EMOJI;
            }
            drawEmoji(g2, emoji, target.x, target.y);
        }

        private void drawEye(Graphics2D g2) {
            drawEmoji(g2, EYE_EMOJI, eyeX, eyeY);
        }

        private void drawInactiveBeam(Graphics2D g2) {
            g2.setFont(statusFont);
            g2.setColor(new Color(140, 140, 255));
            int maxRange = computeBeamRange();
            for (int step = 1; step <= maxRange; step++) {
                int x = eyeX + (int) Math.round(Math.cos(angleRad) * step);
                int y = eyeY - (int) Math.round(Math.sin(angleRad) * step);
                if (step % 2 == 0) {
                    drawCellSymbol(g2, "·", x, y);
                } else {
                    drawCellSymbol(g2, "•", x, y);
                }
            }
        }

        private void drawActiveBeam(Graphics2D g2) {
            int renderRange = Math.min(beamLength, computeBeamRange());
            g2.setFont(emojiFont);
            g2.setColor(new Color(255, 128, 40));
            for (int step = 1; step <= renderRange; step++) {
                int x = eyeX + (int) Math.round(Math.cos(angleRad) * step);
                int y = eyeY - (int) Math.round(Math.sin(angleRad) * step);
                drawEmoji(g2, "⚡", x, y);
            }
        }

        private void drawExplosion(Graphics2D g2) {
            int radius = 1 + (explosionFrames / 3);
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    int x = target.x + dx;
                    int y = target.y + dy;
                    if (!isInside(x, y)) {
                        continue;
                    }
                    double distance = Math.hypot(dx, dy);
                    if (distance <= radius + 0.2) {
                        drawEmoji(g2, EXPLOSION_EMOJI, x, y);
                    }
                }
            }
        }

        private void drawCellSymbol(Graphics2D g2, String symbol, int gridX, int gridY) {
            int cx = gridX * CELL_SIZE + CELL_SIZE / 2;
            int cy = gridY * CELL_SIZE + CELL_SIZE / 2;
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(symbol);
            int h = fm.getAscent();
            g2.drawString(symbol, cx - w / 2, cy + h / 3);
        }

        private void drawEmoji(Graphics2D g2, String emoji, int gridX, int gridY) {
            int cx = gridX * CELL_SIZE + CELL_SIZE / 2;
            int cy = gridY * CELL_SIZE + CELL_SIZE / 2;
            g2.setFont(emojiFont);
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(emoji);
            int h = fm.getAscent();
            g2.drawString(emoji, cx - w / 2, cy + h / 3);
        }

        private void drawStatus(Graphics2D g2) {
            g2.setFont(statusFont);
            g2.setColor(Color.WHITE);
            int statusY = GRID_HEIGHT * CELL_SIZE + 24;
            g2.drawString("Angle: " + String.format("%.1f°", Math.toDegrees(angleRad)), 8, statusY);
            g2.drawString("Velocity: " + String.format("%.2f", angularVelocity), 180, statusY);
            g2.drawString("Controls: " + (leftDown ? "LEFT " : "") + (rightDown ? "RIGHT " : "") + (shootDown ? "SHOOT" : ""), 340, statusY);
            g2.drawString("Target: " + target.emoji + " at (" + target.x + ", " + target.y + ")", 8, statusY + 20);
            g2.drawString("Press A / ← and D / → to steer, Space to fire, Q to quit", 8, statusY + 40);
        }
    }

    private static boolean isInside(int x, int y) {
        return x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT;
    }

    private static final class ControlOutput {
        final double angularAcceleration;
        final boolean shooting;

        ControlOutput(double angularAcceleration, boolean shooting) {
            this.angularAcceleration = angularAcceleration;
            this.shooting = shooting;
        }
    }

    private static final class Target {
        final int x;
        final int y;
        final String emoji;

        Target(int x, int y, String emoji) {
            this.x = x;
            this.y = y;
            this.emoji = emoji;
        }
    }
}
