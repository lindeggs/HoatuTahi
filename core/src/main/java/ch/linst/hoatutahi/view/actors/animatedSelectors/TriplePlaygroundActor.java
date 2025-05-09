package ch.linst.hoatutahi.view.actors.animatedSelectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import ch.linst.hoatutahi.view.actors.PlaygroundActor;

/**
 * This class takes three PlaygroundActor instances and adds them to its group.
 * Always one of the three actors is visible.
 */
public class TriplePlaygroundActor extends Group {

    Actor pgActors[] = new PlaygroundActor[3];

    public TriplePlaygroundActor(PlaygroundActor pgActorLvl0, PlaygroundActor pgActorLvl1, PlaygroundActor pgActorLvl2, int showLvl) {

        pgActors[0] = pgActorLvl0;
        pgActors[1] = pgActorLvl1;
        pgActors[2] = pgActorLvl2;

        setSize(pgActorLvl0.getWidth(), pgActorLvl0.getHeight());

        showPgActorLvl(showLvl);

        addActor(pgActorLvl0);
        addActor(pgActorLvl1);
        addActor(pgActorLvl2);
    }

    public void showPgActorLvl(int lvl){

        if(lvl >= pgActors.length){
            throw new IllegalArgumentException("lvl index out of bounds");
        }

        for(Actor act: pgActors) act.setVisible(false);
        pgActors[lvl].setVisible(true);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        for(Actor act: pgActors) act.setSize(width, height);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        for(Actor act: pgActors) act.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        for(Actor act: pgActors) act.setHeight(height);
    }
}
