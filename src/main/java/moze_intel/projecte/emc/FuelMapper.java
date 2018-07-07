package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.integration.jei.collector.FuelUpgradeRecipe;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FuelMapper 
{
	private static final List<SimpleStack> FUEL_MAP = new ArrayList<>();
	private static List<FuelUpgradeRecipe> fuelUpgradeRecipes = new ArrayList<>();


	public static void loadMap()
	{
		FUEL_MAP.clear();

		addToMap(new ItemStack(Items.COAL, 1, 1));
		addToMap(new ItemStack(Items.REDSTONE));
		addToMap(new ItemStack(Blocks.REDSTONE_BLOCK));
		addToMap(new ItemStack(Items.COAL));
		addToMap(new ItemStack(Blocks.COAL_BLOCK));
		addToMap(new ItemStack(Items.GUNPOWDER));
		addToMap(new ItemStack(Items.GLOWSTONE_DUST));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 0));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 0));
		addToMap(new ItemStack(Items.BLAZE_POWDER));
		addToMap(new ItemStack(Blocks.GLOWSTONE));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 2));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 2));
		
		FUEL_MAP.sort(Comparator.comparing(EMCMapper::getEmcValue));

		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.COAL, 1, 1), getFuelUpgrade(new ItemStack(Items.COAL, 1, 1))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.REDSTONE), getFuelUpgrade(new ItemStack(Items.REDSTONE))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Blocks.REDSTONE_BLOCK), getFuelUpgrade(new ItemStack(Blocks.REDSTONE_BLOCK))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.COAL), getFuelUpgrade(new ItemStack(Items.COAL))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Blocks.COAL_BLOCK), getFuelUpgrade(new ItemStack(Blocks.COAL_BLOCK))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.GUNPOWDER), getFuelUpgrade(new ItemStack(Items.GUNPOWDER))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.GLOWSTONE_DUST), getFuelUpgrade(new ItemStack(Items.GLOWSTONE_DUST))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuels, 1, 0), getFuelUpgrade(new ItemStack(ObjHandler.fuels, 1, 0))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuelBlock, 1, 0), getFuelUpgrade(new ItemStack(ObjHandler.fuelBlock, 1, 0))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Items.BLAZE_POWDER), getFuelUpgrade(new ItemStack(Items.BLAZE_POWDER))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(Blocks.GLOWSTONE), getFuelUpgrade(new ItemStack(Blocks.GLOWSTONE))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuels, 1, 1), getFuelUpgrade(new ItemStack(ObjHandler.fuels, 1, 1))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuelBlock, 1, 1), getFuelUpgrade(new ItemStack(ObjHandler.fuelBlock, 1, 1))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuels, 1, 2), getFuelUpgrade(new ItemStack(ObjHandler.fuels, 1, 2))));
		fuelUpgradeRecipes.add(new FuelUpgradeRecipe(new ItemStack(ObjHandler.fuelBlock, 1, 2), getFuelUpgrade(new ItemStack(ObjHandler.fuelBlock, 1, 2))));
	}
	
	private static void addToMap(ItemStack stack)
	{
		if (EMCHelper.doesItemHaveEmc(stack))
		{
			addToMap(new SimpleStack(stack));
		}
	}
	
	public static boolean isStackFuel(ItemStack stack)
	{
		return mapContains(new SimpleStack(stack));
	}
	
	public static boolean isStackMaxFuel(ItemStack stack)
	{
		return FUEL_MAP.indexOf(new SimpleStack(stack)) == FUEL_MAP.size() - 1;
	}
	
	public static ItemStack getFuelUpgrade(ItemStack stack)
	{
		SimpleStack fuel = new SimpleStack(stack);

		int index = FUEL_MAP.indexOf(fuel);
		
		if (index == -1)
		{
			PECore.LOGGER.warn("Tried to upgrade invalid fuel: {}", stack);
			return ItemStack.EMPTY;
		}
		
		int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;
		
		return FUEL_MAP.get(nextIndex).toItemStack();
	}

	private static void addToMap(SimpleStack stack)
	{
		if (stack.isValid())
		{
			if (!FUEL_MAP.contains(stack))
			{
				FUEL_MAP.add(stack);
			}
		}
	}

	private static boolean mapContains(SimpleStack stack)
	{
		return stack.isValid() && FUEL_MAP.contains(stack);
	}

	public static List<FuelUpgradeRecipe> getUpgradeRecipes(){
		return fuelUpgradeRecipes;
	}


}
