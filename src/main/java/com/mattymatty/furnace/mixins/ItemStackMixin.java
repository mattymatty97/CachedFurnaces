package com.mattymatty.furnace.mixins;

import com.mattymatty.furnace.FurnaceItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FurnaceItem {
    protected Recipe<?> cachedRecipe;

    public Recipe<?> getCachedRecipe() {
        return cachedRecipe;
    }

    public void setCachedRecipe(Recipe<?> cachedSmeltingRecipe) {
        this.cachedRecipe = cachedSmeltingRecipe;
    }
}
