package moze_intel.projecte.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * This event is fired on both the server and client when a player is attempting to learn a new item
 * This event is cancelable
 * This event is fired on MinecraftForge#EVENT_BUS
 */
@Cancelable
public class PlayerAttemptLearnEvent extends Event
{
    private final EntityPlayer player;
    private final ItemStack stack;

    public PlayerAttemptLearnEvent(@Nonnull EntityPlayer entityPlayer, @Nonnull ItemStack stack)
    {
        player = entityPlayer;
        this.stack = stack;
    }

    /**
     * @return The player who is attempting to learn a new item.
     */
    @Nonnull
    public EntityPlayer getPlayer()
    {
        return player;
    }

    /**
     * @return The stack that the player is trying to learn.
     */
    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }
}
