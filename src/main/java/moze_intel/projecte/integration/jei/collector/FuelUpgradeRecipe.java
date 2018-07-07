package moze_intel.projecte.integration.jei.collector;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.GuiHelper;
import mezz.jei.runtime.JeiHelpers;
import mezz.jei.startup.IModIdHelper;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class FuelUpgradeRecipe implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;
    private long upgradeEMC;

    public FuelUpgradeRecipe(ItemStack input, ItemStack output){
        this.input = input;
        this.output = output;
    }

    public ItemStack getInput(){
        return input;
    }

    public ItemStack getOutput(){
        return output;
    }

    public long getUpgradeEMC(){
        return upgradeEMC;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, getInput());
        ingredients.setOutput(ItemStack.class, getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String emc = Long.toString(getUpgradeEMC());
        FontRenderer fontRenderer = minecraft.fontRenderer;
        int stringWidth = fontRenderer.getStringWidth(emc);
        fontRenderer.drawString(emc, recipeWidth - stringWidth, 0, Color.gray.getRGB());

    }
}