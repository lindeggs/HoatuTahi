package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {

    private final ParticleEffect effect;
    private final Vector2 effectLastPos = new Vector2(0,0);
    private boolean restartAlways = true;

    public ParticleEffectActor(ParticleEffect particleEffectArg) {
        effect = particleEffectArg;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.update(delta);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        effect.scaleEffect(scaleXY);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        effect.setPosition(x, y);
        effectLastPos.set(x, y);
    }

    // Particle effect interface methods

    public void start() {
        effect.start();
        restartAlways = false;
    }

    public void restartAtNewPosition(float x, float y){

        if((effectLastPos.x != x) || (effectLastPos.y != y) || restartAlways){
            setPosition(x, y);
            effect.reset(false);
            effect.start();
            restartAlways = false;
        }
    }

    public void allowCompletion() {
        effect.allowCompletion();
        restartAlways = true;
    }

    public void setAlpha(float alpha){
        //noinspection GDXJavaUnsafeIterator
        for(ParticleEmitter pEmi: effect.getEmitters()){
            pEmi.getTransparency().setHigh(alpha);
        }
    }

}
