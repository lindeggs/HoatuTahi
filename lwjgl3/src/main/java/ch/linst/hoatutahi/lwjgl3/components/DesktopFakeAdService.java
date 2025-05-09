package ch.linst.hoatutahi.lwjgl3.components;

import ch.linst.hoatutahi.components.FakeAdService;

public class DesktopFakeAdService implements FakeAdService {

    private boolean adVisible = true;

    public DesktopFakeAdService() {
    }

    @Override
    public void setVisible(boolean enable) {
        adVisible = enable;
    }

    @Override
    public boolean isVisible() {
        return adVisible;
    }

    @Override
    public int getHeight() {
        return 315;
    }
}
