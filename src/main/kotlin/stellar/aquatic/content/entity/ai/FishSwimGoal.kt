package stellar.aquatic.content.entity.ai

import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import stellar.aquatic.content.entity.AquaticFish

class FishSwimGoal(private val fish: AquaticFish) : RandomSwimmingGoal(fish, 1.0, 40) {
    override fun canUse(): Boolean = fish.canRandomSwim() && super.canUse()
}