package moze_intel.projecte.integration.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.integration.jei.collector.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.WorldTransmutations;

import javax.annotation.Nonnull;

@JEIPlugin
public class PEJeiPlugin implements IModPlugin
{
    public static IJeiRuntime RUNTIME = null;

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {}

    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registry) {}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new CollectorRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        // todo finish this, add alchbag
        registry.addRecipes(WorldTransmutations.getWorldTransmutations(), WorldTransmuteRecipeCategory.UID);
        registry.addRecipes(FuelMapper.getUpgradeRecipes(), CollectorRecipeCategory.UID);

    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
        RUNTIME = jeiRuntime;
    }

}
