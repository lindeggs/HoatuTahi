package ch.linst.hoatutahi.components.gameBackup;

public abstract class BaseFactory {

    protected String getPgHashKey(int sizeX, int sizeY, int level){
        return String.valueOf((100 * level) + (10 * sizeY) + sizeX);
    }
}
