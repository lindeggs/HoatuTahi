package ch.linst.hoatutahi.view.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

public class EndlessSpringMoveToAction extends Action {

    private static final float C_SPRING = 50; // Mouse attraction constant
    private static final float C_FRICTION = 8; // Friction constant

    private final Vector2 currentPos = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Vector2 acceleration = new Vector2();
    private final Vector2 endPos;

    // Support vectors (are class members to avoid the creation of new objects while the action is running)
    private final Vector2 accSpring = new Vector2();
    private final Vector2 accFriction = new Vector2();
    private final Vector2 tmpVect = new Vector2();

    public EndlessSpringMoveToAction(Vector2 endPosArg) {
        super();
        endPos = endPosArg;
    }

    @Override
    public boolean act(float delta) {
        currentPos.set(target.getX(), target.getY()); // Read current location of the target actor

        // Avoid calculation if endpos has been reached (and velocity is also very low)
        if((tmpVect.set(endPos).sub(currentPos).len() > 1f) || (velocity.len() > 10f)){
            accSpring.set(endPos).sub(currentPos).scl(C_SPRING); // Compute the attraction to the end position
            accFriction.set(velocity).scl(-1).scl(C_FRICTION); // Compute deceleration caused by friction
            acceleration.set(accSpring).add(accFriction);

            velocity.add(tmpVect.set(acceleration).scl(delta)); // Velocity changes according to acceleration
            currentPos.add(tmpVect.set(velocity).scl(delta)); // Location changes by velocity
        }
        else{
            currentPos.set(endPos);
            velocity.set(Vector2.Zero);
            acceleration.set(Vector2.Zero);
        }

        target.setPosition(currentPos.x, currentPos.y); // Set new position of the target actor

        return false; // Return false means action is not complete
    }


    public void setEndPosition(float x, float y){
        endPos.set(x, y);
    }

    public void setVelocity(float x, float y){
        velocity.set(x, y);
    }
}
