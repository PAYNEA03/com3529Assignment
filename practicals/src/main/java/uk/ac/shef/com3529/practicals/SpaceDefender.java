package uk.ac.shef.com3529.practicals;

public class SpaceDefender {

    private boolean gameOver, outOfAmmo;
    private int numLives;
    private int numAmmo;
    private int x, y;

    public SpaceDefender(int numLives, int numAmmo, int x, int y) {
        this.numLives = numLives;
        this.gameOver = false;
        this.numAmmo = numAmmo;
        this.outOfAmmo = false;
        this.x = x;
        this.y = y;
    }

    public void gotShot() {
        numLives --;
        if (numLives == 0) {
            gameOver = true;
        }
    }

    public void shoot() {
        if (!gameOver && !outOfAmmo) {
            numAmmo --;
            if (numAmmo == 0) {
                outOfAmmo = true;
            }
        }
    }

    public void powerUp() {
        numLives ++;
    }

    public void ammoBoost(int numAmmo) {
        this.numAmmo += numAmmo;
    }

    public void moveLeft() {
        if (!gameOver && x > 0) {
            x --;
        }
    }

    public void moveRight() {
        if (!gameOver) {
            x ++;
        }
    }

    public void moveUp() {
        if (!gameOver && y > 0) {
            y --;
        }
    }

    public void moveDown() {
        if (!gameOver) {
            y ++;
        }
    }
}
