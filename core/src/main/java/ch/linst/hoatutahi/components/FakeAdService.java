package ch.linst.hoatutahi.components;

/**
 * Provides Operations to switch in app advertisement on or off
 */
public interface FakeAdService {

    public void setVisible(boolean enable);

    public boolean isVisible();

    public int getHeight();
}
