package de.zillolp.cookieclicker.profiles;

public class ClickerGameProfile {
    private long lastClickerInteraction;
    private long lastCommandInteraction;
    private long lastPlayerMove;
    private long lastParticleEffect;
    private int playerClicksPerSecond;
    private SetupState setupState;
    private boolean firstClicked;
    private int frameCount;

    public ClickerGameProfile() {
        long currentTime = System.currentTimeMillis();
        lastClickerInteraction = currentTime;
        lastCommandInteraction = currentTime;
        lastPlayerMove = currentTime;
        lastParticleEffect = currentTime;
        playerClicksPerSecond = 0;
        setupState = SetupState.NONE;
        firstClicked = false;
        frameCount = 0;
    }

    public boolean isOverLastClickerInteraction(long delay) {
        return lastClickerInteraction + delay > System.currentTimeMillis();
    }

    public boolean isOverLastCommandInteraction(long delay) {
        return lastCommandInteraction + delay > System.currentTimeMillis();
    }

    public void updateLastClickerInteraction() {
        lastClickerInteraction = System.currentTimeMillis();
    }

    public void updateLastCommandInteraction() {
        lastCommandInteraction = System.currentTimeMillis();
    }

    public boolean isUnderLastPlayerMove(long delay) {
        return lastPlayerMove + delay < System.currentTimeMillis();
    }

    public void updateLastPlayerMove() {
        lastPlayerMove = System.currentTimeMillis();
    }

    public boolean isOverLastParticleEffect() {
        return lastParticleEffect < System.currentTimeMillis();
    }

    public void updateLastParticleEffect(long delay) {
        lastParticleEffect = System.currentTimeMillis() + delay;
    }

    public boolean isOverCPS(int clicksPerSecond) {
        return getPlayerClicksPerSecond() > clicksPerSecond;
    }

    public int getPlayerClicksPerSecond() {
        return playerClicksPerSecond;
    }

    public void setPlayerClicksPerSecond(int playerClicksPerSecond) {
        this.playerClicksPerSecond = playerClicksPerSecond;
    }

    public void addPlayerClicksPerSecond(int playerClicksPerSecond) {
        setPlayerClicksPerSecond(getPlayerClicksPerSecond() + playerClicksPerSecond);
    }

    public SetupState getSetupState() {
        return setupState;
    }

    public void setSetupState(SetupState setupState, int number) {
        this.setupState = setupState;
        setupState.setNumber(number);
    }

    public boolean isFirstClicked() {
        return firstClicked;
    }

    public void setFirstClicked(boolean firstClicked) {
        this.firstClicked = firstClicked;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public enum SetupState {
        NONE(),
        SET_ALLTIME_HEAD(),
        SET_ALLTIME_SIGN(),
        SET_TIME_HEAD(),
        SET_TIME_SIGN(),
        SET_CLICKER();

        private int number;

        SetupState() {
            this.number = 0;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
