package ch.linst.hoatutahi.view.actors.animatedSelectors;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.playgroundModel.IntRef;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.actors.PlaygroundActor;

public class PgSizeSelectorActor extends SelectorActor {

    private final Integer pgSizeXY[][] = {{2,2}, {3,2}, {3,3}, {4,3}, {4,4}, {5,4}, {5,5}};
    private HoatuTahi hoatuTahi;
    private List<TriplePlaygroundActor> triplePgActors = new ArrayList<>();

    public PgSizeSelectorActor(HoatuTahi hoatuTahiArg) {
        super(hoatuTahiArg.getAssetManager());
        hoatuTahi = hoatuTahiArg;

        for(int i = 0; i < pgSizeXY.length; i++){
            TriplePlaygroundActor triplePgActor = new TriplePlaygroundActor(
                    getPlaygroundActor(pgSizeXY[i][0], pgSizeXY[i][1], 0),
                    getPlaygroundActor(pgSizeXY[i][0], pgSizeXY[i][1], 1),
                    getPlaygroundActor(pgSizeXY[i][0], pgSizeXY[i][1], 2),
                    hoatuTahiArg.getGameFactory().getGameLevel());

            addSelectorItem(triplePgActor);
            triplePgActors.add(triplePgActor);
        }
    }

    public Integer[] getPgSizeXY(){
        return pgSizeXY[getSelectedIndex()];
    }

    public void refreshVisibleGameLevel(){
        for(TriplePlaygroundActor actor: triplePgActors){
            actor.showPgActorLvl(hoatuTahi.getGameFactory().getGameLevel());
        }
    }

    /**
     * Checks if a playground of a certain size and level already exists in the backup.
     * If yes: Returns this playground
     * If no: Creates a new empty playground
     * @param pgSizeX Playground X size
     * @param pgSizeY Playground Y size
     * @param level
     * @return the retrieved or created PlaygroundActor
     */
    private PlaygroundActor getPlaygroundActor(int pgSizeX, int pgSizeY, int level){

        Playground playground;
        PlaygroundActor playgroundActor;

        if(hoatuTahi.getGameFactory().isPlaygroundStored(pgSizeX, pgSizeY, level)){
            // Playground exists in the backup. Resolve it
            playground = hoatuTahi.getGameFactory().getPlayground(pgSizeX, pgSizeY, level);
            playgroundActor = hoatuTahi.getGameViewFactory().getPlaygroundActor(playground, level);
        }
        else{
            // Playground does not exist. Create a new one for the size selector
            playground = new Playground(pgSizeX, pgSizeY, new IntRef(0), new IntRef(0), false);

            int itemSetSelected = hoatuTahi.getGameViewFactory().getItemSetSelected();
            ItemSetContainer itemSetContainer = assetManager.getItemSetContainer(itemSetSelected);

            playgroundActor = new PlaygroundActor(playground, itemSetContainer, level, assetManager);
        }

        return playgroundActor;
    }
}
