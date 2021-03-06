package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.Desire;
import net.mostlyoriginal.game.component.Tutorial;
import net.mostlyoriginal.game.component.module.Entrance;
import net.mostlyoriginal.game.system.common.FluidSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class EntranceSystem extends FluidSystem {

    public static final float MINUTES_TILL_VICTORY_MINUS_ONE_HOUR = 540f;
    private TutorialService tutorialService;

    public EntranceSystem() {
        super(Aspect.all(Entrance.class, Pos.class));
    }

    ClockSystem clockSystem;

    public int spawnsPending = 0;
    public float spawnCooldown = 0;

    @Override
    protected void process(E e) {
        scaleDifficultyWithTime(e);
        considerSpawningVisitor(e);
    }

    private void considerSpawningVisitor(E e) {
        if (tutorialService.step() == Tutorial.Step.DONE) {
            e.entranceCooldown(e.entranceCooldown() - world.getDelta());
            if (e.entranceCooldown() <= 0) {
                e.entranceCooldown(e.entranceTimeBetweenSpawns());
                e.anim(e.interactableStartAnimId());
                int count = MathUtils.random(e.entranceMinCount(), e.entranceMaxCount());
                spawnsPending += count;
            }

            spawnCooldown -= world.delta;
            if (spawnsPending > 0 && spawnCooldown <= 0) {
                spawnCooldown = MathUtils.random(0.2f, 0.3f);
                spawnsPending--;
                spawnVisitor((int) (e.posX() + e.boundsMinx()), (int) (e.posY() - e.boundsMiny()));
            }
        }
    }

    private void scaleDifficultyWithTime(E e) {
        float timeBetweenSpawns = Interpolation.linear.apply(
                e.entranceTimeBetweenSpawnsEasiest(),
                e.entranceTimeBetweenSpawnsHardest(),
                MathUtils.clamp(clockSystem.minutesPassed / MINUTES_TILL_VICTORY_MINUS_ONE_HOUR,0,1f));
        e.entranceTimeBetweenSpawns(
                timeBetweenSpawns);
    }

    private void spawnVisitor(int x, int y) {
        E()
                .pos(x, y)
                .bounds(0,0,GameScreenAssetSystem.VISITOR_WIDTH,GameScreenAssetSystem.DEFAULT_MODULE_HEIGHT)
                .render(GameScreenAssetSystem.LAYER_ACTORS)
                .desire(MathUtils.randomBoolean() ? Desire.Type.POOP : Desire.Type.PEE)
                .anim("visitor");
    }
}
