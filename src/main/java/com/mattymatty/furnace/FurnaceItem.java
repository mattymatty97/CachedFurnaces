package com.mattymatty.furnace;

import net.minecraft.recipe.Recipe;

public interface FurnaceItem {
    Recipe<?> getCachedRecipe();

    void setCachedRecipe(Recipe<?> cachedSmeltingRecipe);
}
